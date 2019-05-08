package zipkinex;

import brave.SpanCustomizer;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Read;

import javax.inject.Inject;


@Controller("/")
public class RootController {

	@Inject
	private SpanCustomizer spanCustomizer;

	@Inject
	private PrometheusMeterRegistry prometheusMeterRegistry;

	private static final Logger log = LoggerFactory.getLogger(RootController.class);


	private final HubClient hubClient;
	private final UsersClient userClient;


	RootController(HubClient hubClient, UsersClient userClient) {
		this.hubClient = hubClient;
		this.userClient = userClient;

	}


	@Get("/")
	public String root() {
		log.debug("Root Hello World");
		return "Hello World from micronaut";
	}

	@Get("/hello")
	public String hello() {
		spanCustomizer.annotate("staticError");
		return "Hello World";
	}

	@Get("/reports")
	public String reports() {
		return hubClient.getStats();
	}

	@Get("/reports/hubCount")
	public String hubCount() {
		int userCount = userClient.getUsers().size();
		int hubCount = hubClient.getHubs().size();
		return "{\"hubs\":{\"" + hubCount + "\":\"users\":\"" + userCount + "}";
	}

	@Get("/admin/prometheus")
	public String scrape() {
		return prometheusMeterRegistry.scrape();
	}

}
