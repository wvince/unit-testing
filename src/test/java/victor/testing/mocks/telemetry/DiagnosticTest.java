package victor.testing.mocks.telemetry;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static victor.testing.mocks.telemetry.Client.*;
import static victor.testing.mocks.telemetry.Client.ClientConfiguration.AckMode.NORMAL;

//@RunWith(MockitoJUnitRunner.class) // JUnit4 equivalent
@ExtendWith(MockitoExtension.class) // JUnit5 extension care se ocupa cu initializarea instantei ede clasa de test
class DiagnosticTest {
  public static final String DIAG_INFO = "DIAG_INFO";
  @Mock
  Client clientMock;
  @InjectMocks // stie sa injecteze cum stie si Spring: contructor/field privat

  Diagnostic diagnostic;

  @Test
  void happy() {
    // given
    when(clientMock.getOnlineStatus()).thenReturn(true); // stubbing "eu stabuiesc o metoda"/ eu mockuiesc
    when(clientMock.receive()).thenReturn(DIAG_INFO);

    // when
    diagnostic.checkTransmission(true);

    // then
    verify(clientMock).disconnect(true); // verific
    verify(clientMock).send(DIAGNOSTIC_MESSAGE); // + mai compact cod
    verify(clientMock).receive(); //are times(1) automat. asta crapa testele acumBUM
    assertThat(diagnostic.getDiagnosticInfo()).isEqualTo(DIAG_INFO);
  }
  
  @Test
  void configuresClient() {
    when(clientMock.getOnlineStatus()).thenReturn(true);

    diagnostic.checkTransmission(true);

    ClientConfiguration config = new ClientConfiguration();
    config.setAckMode(NORMAL); // prima idee, perfect, dar nu merge aici
    verify(clientMock).configure(config);
  }

//  @Test
//  void sendsDiagnostic() { // +e mai narrow ce testezi "Single Assert Rule"
//    when(clientMock.getOnlineStatus()).thenReturn(true); // stubbing "eu stabuiesc o metoda"/ eu mockuiesc
//
//    diagnostic.checkTransmission(true);
//
//    verify(clientMock).send(Client.DIAGNOSTIC_MESSAGE);
//  }

  // in general eu testez tot pe unde trec ('happy flow') iar apoi testez branchurile separate cu @Test
  // si daca sunt anumite behaviouri critice (audit, notificiare), le testez seaprat

  @Test
  void throwsWhenNotOnline() {
    when(clientMock.getOnlineStatus()).thenReturn(false); // stubbing "eu stabuiesc o metoda"/ eu mockuiesc

    assertThatThrownBy(()->diagnostic.checkTransmission(true))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Unable to connect.");

  }
}