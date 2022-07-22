package victor.testing.spring.service.subpachet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import victor.testing.spring.domain.Product;
import victor.testing.spring.domain.ProductCategory;
import victor.testing.spring.domain.Supplier;
import victor.testing.spring.infra.SafetyClient;
import victor.testing.spring.repo.ProductRepo;
import victor.testing.spring.repo.SupplierRepo;
import victor.testing.spring.service.ProductService;
import victor.testing.spring.web.dto.ProductDto;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("db-mem")
@Sql(value = "classpath:/sql/cleanup.sql")
public class ProductServiceSpringTest {
   @MockBean
   public SafetyClient safetyClientMock;
  @Autowired
   private ProductRepo productRepo;
  @Autowired
   private SupplierRepo supplierRepo;
   @Autowired
   private ProductService productService;

//   @Test
//   public void createThrowsForUnsafeProduct() {
//      when(safetyClientMock.isSafe("bar")).thenReturn(false);
//      ProductDto dto = new ProductDto("name", "bar", -1L, ProductCategory.HOME);
//
//      assertThatThrownBy(() -> productService.createProduct(dto))
//              .isInstanceOf(IllegalStateException.class);
//   }

//   @BeforeEach
//   public void curatDupa() {
//      productRepo.deleteAll();
//      supplierRepo.deleteAll(); //acu merge
//   }

   @Test
   public void createOk() {
      // GIVEN
      Long supplierId = supplierRepo.save(new Supplier()).getId();
      when(safetyClientMock.isSafe("safebar")).thenReturn(true);
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
   public void createOkBis() {
      // GIVEN
      Long supplierId = supplierRepo.save(new Supplier()).getId();
      when(safetyClientMock.isSafe("safebar")).thenReturn(true);
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
