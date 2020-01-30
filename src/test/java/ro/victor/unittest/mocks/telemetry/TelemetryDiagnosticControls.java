package ro.victor.unittest.mocks.telemetry;

import java.time.ZoneId;
import java.util.UUID;

import ro.victor.unittest.mocks.telemetry.TelemetryClient.ClientConfiguration;
import ro.victor.unittest.mocks.telemetry.TelemetryClient.ClientConfiguration.AckMode;
import ro.victor.unittest.time.rule.TimeProvider;

public class TelemetryDiagnosticControls {
	public static final String DIAGNOSTIC_CHANNEL_CONNECTION_STRING = "*111#";

	private final TelemetryClient telemetryClient;
	private final ClientConfigurationFactory configFactory;
	private String diagnosticInfo = "";

	public TelemetryDiagnosticControls(TelemetryClient telemetryClient, ClientConfigurationFactory configFactory) {
		this.telemetryClient = telemetryClient;
		this.configFactory = configFactory;
	}


	public String getDiagnosticInfo() {
		return diagnosticInfo;
	}
	public void setDiagnosticInfo(String diagnosticInfo) {
		this.diagnosticInfo = diagnosticInfo;
	}

	public void checkTransmission() throws Exception {
		telemetryClient.disconnect();  // ASTA

		int currentRetry = 1;
		while (! telemetryClient.getOnlineStatus() && currentRetry <= 3) {
			telemetryClient.connect(DIAGNOSTIC_CHANNEL_CONNECTION_STRING);
			currentRetry ++;
		}


		if (! telemetryClient.getOnlineStatus()) {
			throw new IllegalStateException("Unable to connect.");
		}

		telemetryClient.configure(configFactory.createConfig());

		telemetryClient.send(TelemetryClient.DIAGNOSTIC_MESSAGE);
		diagnosticInfo = telemetryClient.receive();
	}

}

