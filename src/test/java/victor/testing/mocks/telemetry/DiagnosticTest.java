package victor.testing.mocks.telemetry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import victor.testing.mocks.telemetry.Client.ClientConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static victor.testing.mocks.telemetry.Client.ClientConfiguration.AckMode.NORMAL;

@ExtendWith(MockitoExtension.class)
public class DiagnosticTest {
    @Mock
    private Client clientMock;
    @InjectMocks
    private Diagnostic target;

//@BeforeAll // 1 data inainte de toate testele
//public static void method() {
//}
    @BeforeEach
    final void before() {
        when(clientMock.getOnlineStatus()).thenReturn(true);
    }

    @Test
    void throwsWhenOffline() {
        when(clientMock.getOnlineStatus()).thenReturn(false);

        assertThatThrownBy(() -> target.checkTransmission(true))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void exploreWithTrue() {
        // given adica contextul testului

        // when adica callu de prod
        target.checkTransmission(true);

        // then adica ce-a facut
        verify(clientMock).disconnect(true);
        verify(clientMock).send(Client.DIAGNOSTIC_MESSAGE); // a in mod normal, unless:
        verify(clientMock).send("AT#UD"); // b sa sufere cel care modifica val constantei :
        // ca sa fie constient ce modifica .. cand  val asta e cunoscuta altor sisteme (DB, FE, Alt microl, Fisier)
    }

    @Test
    void receivesDiagnosticInfo() {
        when(clientMock.receive()).thenReturn("DE CE DOAMNE!?");

        // when
        target.checkTransmission(true);

        assertThat(target.getDiagnosticInfo()).isEqualTo("DE CE DOAMNE!?");
    }

    @Captor
    ArgumentCaptor<ClientConfiguration> configCaptor;

    @Test
    void configuresClient() {
        target.checkTransmission(true);

        verify(clientMock).configure(configCaptor.capture());
        ClientConfiguration config = configCaptor.getValue();
        assertThat(config.getAckMode()).isEqualTo(NORMAL);
    }
}
