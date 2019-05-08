package zipkinex;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller("/")
public class RootController {
	private static final Logger log = LoggerFactory.getLogger(RootController.class);


	private final HubClient hubClient;

	RootController(HubClient hubClient) {
		this.hubClient = hubClient;
	}

	@Get("/")
	public String root() {
		log.debug("Root Hello World");
		return "Hello World from micronaut";
	}

	@Get("/hello")
	public String hello() {
		return "Hello World";
	}

	@Get("/reports")
	public String reports() {
		return hubClient.getStats();
	}

	@Get("/reports/hubCount")
	public String hubCount() {
		return "Hubs: " + hubClient.getHubs().size();
	}
}
