package victor.testing.mocks.telemetry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import victor.testing.mocks.telemetry.Client.ClientConfiguration;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static victor.testing.mocks.telemetry.Client.ClientConfiguration.AckMode.NORMAL;

//class Fake implements I {}
//@MockitoSettings(strictness = Strictness.LENIENT) // greseala> pt ca (re)deschide portita ca sa pui in @Before cu detoate. ,
@ExtendWith(MockitoExtension.class)
public class TelemetryDiagnosticTest {
    @Mock
    Client clientMock;
    @Mock
    ConfigFactory configFactory;
    @InjectMocks
    Diagnostic diagnostic;


    @BeforeEach
    final void before() {
        // stubbing (eu stabuiesc, tu stabuiesti, noi stabuim)
        lenient().when(clientMock.getOnlineStatus()).thenReturn(true);
    }

    @Test
    void throwsWhenNotOnline() {
        when(clientMock.getOnlineStatus()).thenReturn(false);

        assertThatThrownBy(() -> diagnostic.checkTransmission(true))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void happy() {
        when(configFactory.createConfig(any())).thenReturn(new ClientConfiguration());
//        doReturn(new ClientConfiguration()).when(diagnostic).createConfig(any());
//        when(clientMock.getVersion()).thenReturn("nu-mi pasa, da sa fie nenull");
        when(clientMock.receive()).thenReturn("acelceva");

        diagnostic.checkTransmission(true);

        verify(clientMock).disconnect(true);
        verify(clientMock).send(Client.DIAGNOSTIC_MESSAGE);
        assertThat(diagnostic.getDiagnosticInfo()).isEqualTo("acelceva");
    }



}


class ConfigFactoryTest {
    private final ConfigFactory configFactory = new ConfigFactory();
    @Test
    void configuresClient() { // x 7 😊 tests
        ClientConfiguration config = configFactory.createConfig("ver");

        assertThat(config.getAckMode()).isEqualTo(NORMAL);
        assertThat(config.getSessionStart()).isCloseTo(now(), byLessThan(1, MINUTES));
        assertThat(config.getSessionId())
                .startsWith("VER-")
                .hasSize(40)
        ;
    }
}