package victor.testing.mocks.telemetry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import victor.testing.mocks.telemetry.Client.ClientConfiguration;
import victor.testing.mocks.telemetry.Client.ClientConfiguration.AckMode;

import java.time.LocalDateTime;

import static java.util.UUID.randomUUID;

@Component
public class Diagnostic {
	public static final String DIAGNOSTIC_CHANNEL_CONNECTION_STRING = "*111#";
	@Autowired
	private Client client;
	private String diagnosticInfo = "";


	public void checkTransmission(boolean force) {
		client.disconnect(force);

		int currentRetry = 1;
		while (! client.getOnlineStatus() && currentRetry <= 3) {
			client.connect(DIAGNOSTIC_CHANNEL_CONNECTION_STRING);
			currentRetry ++;
		}

		if (! client.getOnlineStatus()) {
			throw new IllegalStateException("Unable to connect.");
		}

		ClientConfiguration config = new ClientConfiguration();
		config.setSessionId(client.getVersion()/*.toUpperCase()*/ + "-" + randomUUID());
		config.setSessionStart(LocalDateTime.now());
		config.setAckMode(AckMode.NORMAL);
		client.configure(config);

		client.send(Client.DIAGNOSTIC_MESSAGE);
		diagnosticInfo = client.receive();
	}

	public String getDiagnosticInfo() {
		return diagnosticInfo;
	}

}
