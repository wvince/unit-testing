package victor.testing.spring;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import victor.testing.spring.product.infra.SafetyClient;
import victor.testing.tools.TestcontainersUtils;

// #1 innocent Testcontainers test (online examples)
@SpringBootTest
@ActiveProfiles("db-mem")
public class BaseDatabaseTest {
//  @MockBean // inlocuieste beanul real SafetyClient cu un Mockito.mock() pe care ti-l pune si aici sa-l configurezi, auto-reset intre @Teste
//  protected SafetyClient safetyClient;
}

// ==================================================================
// #2 innocent Testcontainers test (online examples)
//@SpringBootTest
//@Testcontainers
//public class BaseDatabaseTest {
//  // https://stackoverflow.com/questions/62425598/how-to-reuse-testcontainers-between-multiple-springboottests
//  // === The containers is reused across all subclasses ===
//  static public PostgreSQLContainer<?> postgres =
//      new PostgreSQLContainer<>("postgres:11");
//
//  // TODO add in ~/.testcontainers.properties put testcontainers.reuse.enable=true
//
//  @BeforeAll
//  public static void startTestcontainer() {
//    postgres.start();
//  }
//
//  @DynamicPropertySource
//  public static void registerPgProperties(DynamicPropertyRegistry registry) {
//    // ia detaliile de conectare la DB din docker si le da lu Spring
//    TestcontainersUtils.addDatasourceDetails(registry, postgres, true);
//  }
//}

// ==================================================================
// #3 tuned Testcontainers test (like a platform team will ofer)
//@SpringBootTest
//@ActiveProfiles("db-testcontainers-playtika")
//public class BaseDatabaseTest {
//
//}
