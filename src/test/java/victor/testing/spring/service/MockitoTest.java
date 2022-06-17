package victor.testing.spring.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import victor.testing.spring.domain.Product;
import victor.testing.spring.domain.ProductCategory;
import victor.testing.spring.domain.Supplier;
import victor.testing.spring.infra.SafetyClient;
import victor.testing.spring.repo.ProductRepo;
import victor.testing.spring.repo.SupplierRepo;
import victor.testing.spring.web.dto.ProductDto;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest//(properties = "spring.datasource.url=jdbc:h2:mem:")
@ActiveProfiles("db-mem")
//@Transactional // LOVE❤️
@Sql("classpath:/sql/cleanup.sql")
public class MockitoTest {
   @MockBean // inlocuieste in contextu spring beanul real cu un Mock mockito pe care apoi ti-l si da aici sa-l programezik
   public SafetyClient mockSafetyClient;// = new SafetyClient(new RestTemplate());
   @Autowired
   private ProductRepo productRepo;
   @Autowired
   private SupplierRepo supplierRepo;
   @Autowired
   private ProductService productService;

//@BeforeEach
//final void before() {
//   // ordinea conteaza :)
//    productRepo.deleteAll();
//   supplierRepo.deleteAll();
//}
   @Test
   public void createThrowsForUnsafeProduct() {
      when(mockSafetyClient.isSafe("bar")).thenReturn(false);

      ProductDto dto = new ProductDto("name", "bar", -1L, ProductCategory.HOME);
      assertThatThrownBy(() -> productService.createProduct(dto))
              .isInstanceOf(IllegalStateException.class);
   }

   @Test
   public void createOk() {
      // GIVEN
//      Supplier supplier = new Supplier().setId(13L);
      Long supplierId = supplierRepo.save(new Supplier()).getId();

      when(mockSafetyClient.isSafe("safebar")).thenReturn(true);
      ProductDto dto = new ProductDto("name", "safebar", supplierId, ProductCategory.HOME);

      // WHEN
      productService.createProduct(dto);

      // THEN
      assertThat(productRepo.findAll()).hasSize(1);
      Product product = productRepo.findAll().get(0);

      assertThat(product.getName()).isEqualTo("name");
      assertThat(product.getBarcode()).isEqualTo("safebar");
      assertThat(product.getSupplier().getId()).isEqualTo(supplierId);
      assertThat(product.getCategory()).isEqualTo(ProductCategory.HOME);
      assertThat(product.getCreateDate()).isCloseTo(now(), byLessThan(1, SECONDS));
   }
   @Test
   public void createOk2() {
      // GIVEN
//      Supplier supplier = new Supplier().setId(13L);
      Long supplierId = supplierRepo.save(new Supplier()).getId();

      when(mockSafetyClient.isSafe("safebar")).thenReturn(true);
      ProductDto dto = new ProductDto("name", "safebar", supplierId, ProductCategory.HOME);

      // WHEN
      productService.createProduct(dto);

      // THEN
      assertThat(productRepo.findAll()).hasSize(1);
      Product product = productRepo.findAll().get(0);

      assertThat(product.getName()).isEqualTo("name");
      assertThat(product.getBarcode()).isEqualTo("safebar");
      assertThat(product.getSupplier().getId()).isEqualTo(supplierId);
      assertThat(product.getCategory()).isEqualTo(ProductCategory.HOME);
      assertThat(product.getCreateDate()).isCloseTo(now(), byLessThan(1, SECONDS));
   }

}
