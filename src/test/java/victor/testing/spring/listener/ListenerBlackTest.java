package victor.testing.spring.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import victor.testing.spring.IntegrationTest;
import victor.testing.spring.entity.Supplier;
import victor.testing.spring.repo.SupplierRepo;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static victor.testing.spring.listener.MessageListener.SUPPLIER_CREATED_ERROR;

public class ListenerBlackTest extends IntegrationTest {
  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  SupplierRepo supplierRepo;

  @BeforeEach
  @AfterEach
  final void cleanDB() { // manual cleanup required as tested code COMMITs an INSERT
    supplierRepo.deleteAll();
  }

  @Test
  void supplierIsCreated_sleep_flaky() throws InterruptedException {
    // trigger message
    kafkaTemplate.send("supplier-created-event", "supplier");

    Thread.sleep(200); // It works on my machine™️, said its author

    assertThat(supplierRepo.findByName("supplier"))
        .describedAs("Supplier was inserted")
        .isNotEmpty();
  }

  @Test
  void supplierIsCreated_polling() throws ExecutionException, InterruptedException {
    // trigger message
    kafkaTemplate.send("supplier-created-event", "supplier");

    Awaitility.await() // state of the art in polling
        .pollInterval(ofMillis(5)) // try every 5ms
        .timeout(ofSeconds(1)) // fail after 1s
        .untilAsserted(() ->
            assertThat(supplierRepo.findByName("supplier"))
                .describedAs("Supplier was inserted")
                .isNotEmpty());

  }
  @Test
  void supplierIsCreatedError_blockingReceive() throws ExecutionException, InterruptedException {
    supplierRepo.save(new Supplier().setName("supplier"));
    // trigger message
    kafkaTemplate.send("supplier-created-event", "supplier");
  }

}
