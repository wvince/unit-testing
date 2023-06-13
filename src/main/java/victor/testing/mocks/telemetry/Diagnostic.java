package victor.testing.mocks.telemetry;

import victor.testing.mocks.telemetry.Client.ClientConfiguration;
import victor.testing.mocks.telemetry.Client.ClientConfiguration.AckMode;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class Diagnostic {
	public static final String DIAGNOSTIC_CHANNEL_CONNECTION_STRING = "*111#";
	private final Client client;
	private String diagnosticInfo = "";

	public Diagnostic(Client client) {
		this.client = client;
	}

	public void checkTransmission(boolean force) {
		client.disconnect(force);

		if (! client.getOnlineStatus()) {
			throw new IllegalStateException("Unable to connect.");
		}

		ClientConfiguration config = new ClientConfiguration();
		config.setSessionId(client.getVersion() + "-" + randomUUID());
		config.setSessionStart(LocalDateTime.now());
		config.setAckMode(AckMode.NORMAL);
		client.configure(config);

		client.send(Client.DIAGNOSTIC_MESSAGE);
		RequestObj requestObj = new RequestObj();
		requestObj.a = 117;
		requestObj.b = 246235;
		requestObj.gCritical = "x";
		diagnosticInfo = client.receive(null);
	}

	public String getDiagnosticInfo() {
		return diagnosticInfo;
	}
//	private final Clock clock;
//	private final RandomUUIDGenerator uuidGenerator;
}


// @Bean Clock clock() {}
// @Bean RandomUUIDGenerator uuidGenerator() {}