package victor.testing.spring.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.testing.filebased.message.KafkaMessageSender;
import victor.testing.spring.product.domain.Product;
import victor.testing.spring.product.domain.ProductCategory;
import victor.testing.spring.product.domain.Supplier;
import victor.testing.spring.product.infra.SafetyClient;
import victor.testing.spring.product.repo.ProductRepo;
import victor.testing.spring.product.repo.SupplierRepo;
import victor.testing.spring.product.api.dto.ProductDto;
import victor.testing.spring.product.api.dto.ProductSearchCriteria;
import victor.testing.spring.product.api.dto.ProductSearchResult;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
  public static final String PRODUCT_CREATED_TOPIC = "product-created";
  private final SafetyClient safetyClient;
  private final ProductRepo productRepo;
  private final SupplierRepo supplierRepo;
  private final ProductMapper productMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;

//  @Async
//  @Transactional(propagation = Propagation.REQUIRES_NEW) // BUM
  public void createProduct(ProductDto productDto) {
    boolean safe = safetyClient.isSafe(productDto.getBarcode()); // ⚠️ REST call inside
    if (!safe) {
      throw new IllegalStateException("Product is not safe: " + productDto.getBarcode());
    }
    if (productDto.getCategory() == null) {
      productDto.setCategory(ProductCategory.UNCATEGORIZED); // untested line 😱
    }
    Product product = new Product();
    product.setName(productDto.getName());
    product.setBarcode(productDto.getBarcode());
    product.setCategory(productDto.getCategory());
//    Supplier supplier = supplierRepo.findById(productDto.getSupplierId()).orElseThrow();
//    Supplier supplier = supplierRepo.getReferenceById(productDto.getSupplierId());
    Supplier supplier = new Supplier().setId(productDto.getSupplierId()); // SOC, tot merge.
    product.setSupplier(supplier);
    productRepo.save(product);
    kafkaTemplate.send(PRODUCT_CREATED_TOPIC, "k", product.getName().toUpperCase());
  }

  public List<ProductSearchResult> searchProduct(ProductSearchCriteria criteria) {
    return productRepo.search(criteria);
  }

  public ProductDto getProduct(long productId) {
    Product product = productRepo.findById(productId).orElseThrow();
    return new ProductDto()
        .setId(product.getId())
        .setSupplierId(product.getSupplier().getId())
        .setName(product.getName())
        .setBarcode(product.getBarcode())
        .setCategory(product.getCategory())
        .setCreateDate(product.getCreateDate());
  }
}
