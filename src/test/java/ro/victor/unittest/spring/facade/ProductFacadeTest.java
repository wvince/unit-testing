package ro.victor.unittest.spring.facade;

//import org.junit.Before;
//import org.junit.Test;
//import org.junit.Test;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ro.victor.unittest.spring.domain.Product;
import ro.victor.unittest.spring.domain.Supplier;
import ro.victor.unittest.spring.infra.WhoServiceClient;
import ro.victor.unittest.spring.repo.ProductRepo;
import ro.victor.unittest.spring.web.ProductDto;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductFacadeTest {
    @Autowired
    private ProductFacade facade;
    @Autowired
    private ProductRepo productRepo;
    @MockBean
    private WhoServiceClient externalServiceClient;
    @MockBean
    private Clock clock;

    private Instant instant = Instant.now();

    @Before
    public void setupDefaultClock() {
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
//        when(clock.instant()).thenReturn(instant);
        when(clock.instant()).thenAnswer(invocation -> instant);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsForCovidVaccineTrue() {
        Product product = new Product();
        productRepo.save(product);
        when(externalServiceClient.covidVaccineExists()).thenReturn(true);
        facade.getProduct(product.getId());
    }
    @Test(expected = IllegalStateException.class)
    public void throwsForSupplierInactive() {
        Product product = new Product()
                .setSupplier(new Supplier());
        productRepo.save(product);
        when(externalServiceClient.covidVaccineExists()).thenReturn(false);
        facade.getProduct(product.getId());
    }
    @Test
    public void returnsCorrectly() {


        LocalDateTime testTime = LocalDateTime.now();
        Product product = new Product("numeX")
                .setSampleDate(testTime)
                .setSupplier(new Supplier().setActive(true));
        productRepo.save(product);
        when(externalServiceClient.covidVaccineExists()).thenReturn(false);
        ProductDto dto = facade.getProduct(product.getId());
        assertThat(dto.productName).isEqualTo("numeX");
//        assertThat(dto.sampleDate).isNotNull(); -- inceputuri.
        assertThat(dto.sampleDate).isEqualTo(testTime.format(DateTimeFormatter.ISO_DATE_TIME));
    }
    @Test
    public void returnsCorrectlyForNoProductSampleDate() throws InterruptedException {
        Instant testTime = Instant.now();
//        reset(clock);
//        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
//        when(clock.instant()).thenReturn(testTime.toInstant(ZoneOffset.from(testTime)));
        instant = testTime;

        Product product = new Product()
                .setSupplier(new Supplier().setActive(true));
        productRepo.save(product);
        Thread.sleep(100);
        when(externalServiceClient.covidVaccineExists()).thenReturn(false);
        ProductDto dto = facade.getProduct(product.getId());
        assertThat(dto.sampleDate).isEqualTo(
                LocalDateTime.now(clock).format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
