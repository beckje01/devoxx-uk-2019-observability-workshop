
import ratpack.handling.internal.NcsaRequestLogger
import ratpack.hub.HubsChain
import org.slf4j.Logger;
import org.slf4j.LoggerFactory
import ratpack.report.ReportsChain
import ratpack.user.UsersChain
import workshop.LoggingResponseHandler

import static ratpack.groovy.Groovy.ratpack

Logger log = LoggerFactory.getLogger("ratpack.groovy");


ratpack {
	bindings {
		add(LoggingResponseHandler.decorator())
		bind(UsersChain)
		bind(HubsChain)
		bind(ReportsChain)


	}

	serverConfig {
		port(5051)

	}

	handlers {
		all(new NcsaRequestLogger(log))

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
