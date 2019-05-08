import org.slf4j.MDC
import ratpack.handling.internal.NcsaRequestLogger
import ratpack.http.client.HttpClient
import ratpack.hub.HubsChain

import org.slf4j.Logger;
import org.slf4j.LoggerFactory
import ratpack.report.ReportsChain
import ratpack.user.UsersChain
import workshop.LoggingContext
import workshop.LoggingResponseHandler;

import static ratpack.groovy.Groovy.ratpack

Logger log = LoggerFactory.getLogger("ratpack.groovy");


ratpack {
	bindings {
		bind(UsersChain)
		bind(HubsChain)
		bind(ReportsChain)
	}

	serverConfig {
		port(5051)

	}

	handlers {
		get {
			render "EDGE"
		}

		prefix("users") {
			all chain(UsersChain)
		}

		prefix("hubs") {
			all chain(HubsChain)
		}

		prefix("reports") {
			all chain(ReportsChain)
		}


		files { dir "public" }
	}
}
