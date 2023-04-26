package victor.testing.mocks.telemetry;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@RunWith(MockitoJUnitRunner.class) // 4
@ExtendWith(MockitoExtension.class) // 5
class DiagnosticTest {
  @Mock
  Client clientMock; // = mock(...)
  @InjectMocks
  Diagnostic diagnostic; // inejcteaza orice @Mock de mai sus oricum poate (ctor, setter, private fields)
  // < oricum stie Spring sa faca DI, stie si Mockito

  @Test
  void disconnects() {
    // given
    when(clientMock.getOnlineStatus()).thenReturn(true);

    // when
    diagnostic.checkTransmission(true);

    // then
    verify(clientMock).disconnect(true);
  }

  @Test
  void throwsWhenNotOnline() {
    // given
    when(clientMock.getOnlineStatus()).thenReturn(false);

    // when
    assertThatThrownBy(() -> diagnostic.checkTransmission(true))
            .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void sendsDiagnosticMessage() {
    // given
    when(clientMock.getOnlineStatus()).thenReturn(true);

    // when
    diagnostic.checkTransmission(true);

    verify(clientMock).send(Client.DIAGNOSTIC_MESSAGE);
//    verify(clientMock).send(anyString()); // nu-ti pasa/ e f dificil sa vf exact ce arg / deja ai verificat in alt test detaliile argumentului
  }
}