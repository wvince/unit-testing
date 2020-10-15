package victor.testing.mocks.telemetry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import victor.testing.mocks.telemetry.TelemetryClient.ClientConfiguration;
import victor.testing.mocks.telemetry.TelemetryClient.ClientConfiguration.AckMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class TelemetryDiagnosticControlsTest {
	@Mock
	private TelemetryClient client;
	@InjectMocks
	private TelemetryDiagnosticControls controls;

	@Before
	public void before() {
//		controls = new TelemetryDiagnosticControls(client, "STUFF");
		lenient().when(client.getVersion()).thenReturn("i don't care");
		when(client.getOnlineStatus()).thenReturn(true);
	}

	
	
	@Test(expected = IllegalStateException.class)
	public void throwsWhenNotOnline() {
		when(client.getOnlineStatus()).thenReturn(false);
		controls.checkTransmission();
	}

	
	@Test
	public void disconnects() {
		controls.checkTransmission();
		verify(client).disconnect();
	}

	@Test
	public void sendsDiagnosticInfo() {
		controls.checkTransmission();
		verify(client).send(TelemetryClient.DIAGNOSTIC_MESSAGE);
	}

	// HOMEWORK: what line in the test below is useless
	@Test
	public void receivesDiagnosticInfo() {
		// TODO inspect
		when(client.getOnlineStatus()).thenReturn(true);
		when(client.receive()).thenReturn("kalimera");
		controls.checkTransmission();
//		verify(client).receive(); // 99% cases is useless
		// 1% ? == 
//		verify(client/* , Mockito.times(1) default*/).receive(); // expensive extenal call: you might one
//		verify(client).receive(captor); // B) when you captor
		assertThat(controls.getDiagnosticInfo()).isEqualTo("kalimera");
	}

	@Captor
	private ArgumentCaptor<ClientConfiguration> configCaptor;
	
	@Test
	public void configuresClient() throws Exception {
		ClientConfiguration config = controls.createConfig("ver");
		assertThat(config.getAckMode()).isEqualTo(AckMode.NORMAL);
		assertThat(config.getSessionId()).startsWith("VER-");
		assertThat(config.getSessionStart()).isNotNull(); // engineering
	}
	
	
	// more strict engineers
//		LocalDateTime expectedDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
//		LocalDateTime actualDay = config.getSessionStart().truncatedTo(ChronoUnit.DAYS);
//		assertThat(actualDay).isEqualTo(expectedDay);
}
