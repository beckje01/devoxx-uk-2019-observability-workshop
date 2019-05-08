package workshop;

public class LoggingContext {

	public static final String LOGGING_ID_KEY = "loggingId";
	public static final String LOGGING_HEADER_NAME = "X-LOGGING-ID";

	private String loggingId;

	public LoggingContext(String loggingId) {

		this.loggingId = loggingId;
	}

	public String getLoggingId() {
		return loggingId;
	}

	public void setLoggingId(String loggingId) {
		this.loggingId = loggingId;
	}
}
