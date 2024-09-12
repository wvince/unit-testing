package victor.testing.spring.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import victor.testing.spring.entity.Product;
import victor.testing.spring.entity.Supplier;
import victor.testing.spring.infra.SafetyApiAdapter;
import victor.testing.spring.repo.ProductRepo;
import victor.testing.spring.repo.SupplierRepo;
import victor.testing.spring.rest.dto.ProductDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static victor.testing.spring.service.ProductService.PRODUCT_CREATED_TOPIC;

@ExtendWith(MockitoExtension.class) // asta interpreteaza @ din clasa
class ProductServiceTest {
  public static final String BARCODE = "#barcode#";
  @Mock
  SafetyApiAdapter apiAdapter;
  @Mock
  SupplierRepo supplierRepo;
  @Mock
  ProductRepo productRepo;
  @Mock
  KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
  @InjectMocks
  ProductService target;

  @Test
//  void whenProductIsNotSafe_createShouldThrowIllegalStateException() {
//  void create_whenProductIsNotSafe_shouldThrowIllegalStateException() {
  void createThrows_forUnsafeProduct() {
    ProductDto dto = new ProductDto();
    dto.setBarcode(BARCODE);
    when(apiAdapter.isSafe(BARCODE)).thenReturn(false);

    assertThrows(IllegalStateException.class, () ->
        target.createProduct(dto));
  }

  @Test
  void createProduct() {
    Supplier supplier = mock(Supplier.class); // RAU
    ProductDto dto = new ProductDto()
        .setBarcode(BARCODE)
        .setSupplierCode("#supplierCode#");
    when(apiAdapter.isSafe(BARCODE)).thenReturn(true);
    when(supplierRepo.findByCode("#supplierCode#"))
        .thenReturn(Optional.of(supplier));
    when(productRepo.save(any()))
        .thenReturn(new Product().setId(13L));

    target.createProduct(dto);

    ArgumentCaptor<ProductCreatedEvent> captor = forClass(ProductCreatedEvent.class);
    verify(kafkaTemplate).send(
        eq(PRODUCT_CREATED_TOPIC),
        eq("k"),
//        any()
        captor.capture()
    );
    ProductCreatedEvent event = captor.getValue();
    assertEquals(13L, event.productId());
  }
}