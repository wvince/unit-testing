package victor.testing.mocks.telemetry;

import com.sun.xml.bind.v2.TODO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import victor.testing.mocks.telemetry.Client.ClientConfiguration;
import victor.testing.mocks.telemetry.Client.ClientConfiguration.AckMode;
import victor.testing.tools.WireMockExtension;

import javax.annotation.RegEx;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


//@AutoConfigureWireMock // spring manages to reuse the same WM between test classes
@ExtendWith(MockitoExtension.class) // this extension to Junit reflection on the fields and sees @Mock @InjectMocks
public class DiagnosticTest {
   @InjectMocks
   private Diagnostic diagnostic;
   @Mock
   private Client client;

   @Test
   void throwsWhenNotOnline() {
      // an unstubbed mock method returns the 'null' value for the type,
      //  boolean=false, Boolean=null, List=emptyList
      assertThatThrownBy(() -> diagnostic.checkTransmission(true))
              .isInstanceOf(IllegalStateException.class)
              .hasMessageContaining("connect") // checking exception
      // PRO: same exception with different reasons
      // CONS: brittle fragile test becasue I test presentation
      // "fix": use ENUMs to distinguish betw error codes
      ;

   }

   @Test
   void disconnects() {
      when(client.getOnlineStatus()).thenReturn(true); // "mock a method" = "stubbing": i am teaching a method what return

      diagnostic.checkTransmission(true);

      verify(client).disconnect(true); // verify the call of a side-effecing function
   }


   @Test
   void sendsDiagnosticMessage() {
      when(client.getOnlineStatus()).thenReturn(true); // "mock a method" = "stubbing": i am teaching a method what return

      diagnostic.checkTransmission(true);

      verify(client).send(Client.DIAGNOSTIC_MESSAGE); // <- default 99%

      verify(client).send("AT#UD"); // <- only if you want to freeze that value, ie. fail the constant value changes
   }

   @Test
   void receives() {
      // given <- use comments for test >7-10 lines long
      when(client.getOnlineStatus()).thenReturn(true); // "mock a method" = "stubbing": i am teaching a method what return
      when(client.receive()).thenReturn("Gyros");

      // when
      diagnostic.checkTransmission(true);

//      verify(client).receive(); // insufficient, actually USELESS
      assertThat(diagnostic.getDiagnosticInfo())
              .isEqualTo("Gyros");
   }

//   @Test
//   void achModeNormal() {
//      when(client.getOnlineStatus()).thenReturn(true); // "mock a method" = "stubbing": i am teaching a method what return
//      when(client.getVersion()).thenReturn("ver");
//
//      diagnostic.checkTransmission(true);
//
//      // extract the value of the parameter from the mock
//      verify(client).configure(configCaptor.capture());
//      ClientConfiguration config = configCaptor.getValue();
//
//      assertThat(config.getAckMode()).isEqualTo(AckMode.NORMAL);
////      assertThat(config.getSessionStart()).isEqualTo(now()); // fails due to millisconds passing from prod up to here
//      assertThat(config.getSessionStart()).isCloseTo(now(), byLessThan(1, SECONDS)); // scientist
//      assertThat(config.getSessionStart()).isNotNull(); // engineer will do
//
//      System.out.println(config.getSessionId());
//      assertThat(config.getSessionId())
//              .startsWith("ver-")
//              .hasSize(40); // approval testing: we copy the actual in the test after manually (visually) checking the result
//      //  "freeze-test"
//
//      // or if you want to test a single field
//      verify(client).configure(argThat(c-> c.getAckMode() == AckMode.NORMAL));
//   }

   @Captor
   ArgumentCaptor<ClientConfiguration> configCaptor;


// CR: the version for the client upper case in the session ID
   @Test
   void createConfigDirectly() {
      when(client.getVersion()).thenReturn("ver");

      ClientConfiguration config = diagnostic.createConfig();

      assertThat(config.getAckMode()).isEqualTo(AckMode.NORMAL);
      assertThat(config.getSessionStart()).isCloseTo(now(), byLessThan(1, SECONDS)); // scientist
      assertThat(config.getSessionId())
              .startsWith("VER-")
              .hasSize(40);
   } // x 11 more such tests


}

// TODO:
// test naming
// single assert rule
// abusing before / resetting mocks
// captor
// asserting exceptions
