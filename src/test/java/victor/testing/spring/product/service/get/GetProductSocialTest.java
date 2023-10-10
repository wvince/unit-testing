package victor.testing.spring.product.service.get;

import org.junit.jupiter.api.Test;
import victor.testing.spring.product.domain.Product;
import victor.testing.spring.product.domain.Supplier;
import victor.testing.spring.product.repo.ProductRepo;
import victor.testing.spring.product.service.ProductMapper;
import victor.testing.spring.product.service.create.ProductService;
import victor.testing.spring.product.api.dto.ProductDto;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static victor.testing.spring.product.domain.ProductCategory.HOME;

public class GetProductSocialTest {
  private ProductRepo productRepo = mock(ProductRepo.class);
  private ProductService productService = new ProductService(
      null,
      productRepo,
      null,
      new ProductMapper(),
      null);

  @Test
  void service_plus_mapper() {
    LocalDate date = LocalDate.now();
    Product product = new Product()
            .setId(1L)
            .setName("name")
            .setSku("SKU")
            .setCategory(HOME)
            .setCreatedDate(date)
            .setSupplier(new Supplier().setId(2L));
    when(productRepo.findById(1L)).thenReturn(Optional.of(product));

    ProductDto dto = productService.getProduct(1L);

    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getName()).isEqualTo("name");
    assertThat(dto.getSku()).isEqualTo("SKU");
    assertThat(dto.getCategory()).isEqualTo(HOME);
    assertThat(dto.getCreatedDate()).isEqualTo(date);
    assertThat(dto.getSupplierId()).isEqualTo(2L);
  }

}
