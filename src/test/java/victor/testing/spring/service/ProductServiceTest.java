package victor.testing.spring.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import victor.testing.spring.domain.Product;
import victor.testing.spring.domain.ProductCategory;
import victor.testing.spring.domain.Supplier;
import victor.testing.spring.infra.SafetyClient;
import victor.testing.spring.repo.ProductRepo;
import victor.testing.spring.repo.SupplierRepo;
import victor.testing.spring.web.dto.ProductDto;
import victor.testing.tools.TestcontainersUtils;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static victor.testing.spring.domain.ProductCategory.HOME;


// non-isolated tests that leave data after themselves in the DB
// 1) findById
// 2) @BeforeEach (instead of @AfterEach) -> not parallel-friendly
// 3) @Sql best for legacy or large databases  -> not parallel-friendly
// 4) 👑 @Transactional you can run tests in parallel
   // BUT:
      // ⚠️ [bad practice] there are some CHECK constraints or PRE-COMMIT triggers that don't run, typical for PL/SQL 100k
      // ⚠️ @Transactional(propagation = REQUIRES_NEW|NOT_SUPPORTED) -> suspend the TEST transaction > COMMIT
      // ⚠️ Loose the thread >  loose the JDBC transaction: if the tested code jumps on another thread > COMMIT
@SpringBootTest
//@ActiveProfiles("db-mem") // bad because:
   // - you can't use NATIVE Oracele SQL - Oracle specific functions , CONNECT BY
   // - you can't test incremental DB scripts

//@Sql(scripts = "classpath:/sql/cleanup.sql", executionPhase = BEFORE_TEST_METHOD)
@Transactional // in tests tells spring to rollback after each @Test
@Testcontainers
public class ProductServiceTest {
   @MockBean // replaces in spring context the bean with a mock
   public SafetyClient mockSafetyClient;
   @Autowired
   private ProductRepo productRepo;
   @Autowired
   private SupplierRepo supplierRepo;
   @Autowired
   private ProductService productService;

   // https://stackoverflow.com/questions/62425598/how-to-reuse-testcontainers-between-multiple-springboottests
   // === The containers is reused across all subclasses ===
   static public PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
           "postgres:11")
     .withReuse(true); // BE CAREFUL to allow testcontainers to reuse the container across different test classes

   @BeforeAll
   public static void startTestcontainer() {
      System.out.println("(re)Starting testcontainer");
      postgres.start();
   }

   @DynamicPropertySource
   public static void registerPgProperties(DynamicPropertyRegistry registry) {
      TestcontainersUtils.addDatasourceDetails(registry, postgres, true);
   }


   //   @BeforeEach
//   final void before() {
//      productRepo.deleteAll();
//       supplierRepo.deleteAll(); // FK order matters
//   }

   @Test
   public void createThrowsForUnsafeProduct() {
      when(mockSafetyClient.isSafe("bar")).thenReturn(false);

      ProductDto dto = new ProductDto("name", "bar", -1L, HOME);
      assertThatThrownBy(() -> productService.createProduct(dto))
              .isInstanceOf(IllegalStateException.class);
   }

   @Test
   public void createOk() {
      Long supplierId = supplierRepo.save(new Supplier()).getId();
      when(mockSafetyClient.isSafe("safebar")).thenReturn(true);
      ProductDto dto = new ProductDto("name", "safebar", supplierId, HOME);

      // when
      productService.createProduct(dto);

      assertThat(productRepo.findAll()).hasSize(1);
      Product product = productRepo.findAll().get(0);
      assertThat(product.getName()).isEqualTo("name");
      assertThat(product.getBarcode()).isEqualTo("safebar");
      assertThat(product.getSupplier().getId()).isEqualTo(supplierId);
      assertThat(product.getCategory()).isEqualTo(HOME);
      assertThat(product.getCreateDate()).isCloseTo(now(), byLessThan(1, SECONDS));
   }
   @Test
   public void createOk2() {
      Long supplierId = supplierRepo.save(new Supplier()).getId();
      when(mockSafetyClient.isSafe("safebar")).thenReturn(true);
      ProductDto dto = new ProductDto("name", "safebar", supplierId, HOME);

      // when
      productService.createProduct(dto);

      assertThat(productRepo.findAll()).hasSize(1);
      Product product = productRepo.findAll().get(0);
      assertThat(product.getName()).isEqualTo("name");
      assertThat(product.getBarcode()).isEqualTo("safebar");
      assertThat(product.getSupplier().getId()).isEqualTo(supplierId);
      assertThat(product.getCategory()).isEqualTo(HOME);
      assertThat(product.getCreateDate()).isCloseTo(now(), byLessThan(1, SECONDS));
   }

}
// alternative: "test data slices" : each test creates from scratch its data

      // if I cannot guarantee the DB is empty at the start of the test
      // > I could TRUNCATE the tables at start of each test. how about multithreaded tests ?!
            // then use the same findAll()

      // > if the tested method returns the ID, then do this:
//      Product product = productRepo.findById(returnedId).orElseThrow();
