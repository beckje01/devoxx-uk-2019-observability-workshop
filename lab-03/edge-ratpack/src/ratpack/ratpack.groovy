import brave.SpanCustomizer
import brave.sampler.Sampler
import org.slf4j.MDC
import ratpack.dropwizard.metrics.DropwizardMetricsModule
import ratpack.dropwizard.metrics.MetricsPrometheusHandler
import ratpack.handling.internal.NcsaRequestLogger
import ratpack.http.client.HttpClient
import ratpack.hub.HubsChain

import org.slf4j.Logger;
import org.slf4j.LoggerFactory
import ratpack.report.ReportsChain
import ratpack.user.UsersChain
import ratpack.zipkin.ServerTracingModule
import workshop.LoggingContext
import workshop.LoggingResponseHandler
import zipkin2.reporter.AsyncReporter
import zipkin2.reporter.okhttp3.OkHttpSender;

import static ratpack.groovy.Groovy.ratpack

Logger log = LoggerFactory.getLogger("ratpack.groovy");


ratpack {
	bindings {
		add(LoggingResponseHandler.decorator())
		bind(UsersChain)
		bind(HubsChain)
		bind(ReportsChain)

		module(ServerTracingModule, { config ->
			config
					.serviceName("edge")
					.sampler(Sampler.ALWAYS_SAMPLE)
					.spanReporterV2(AsyncReporter.create(OkHttpSender.create("http://localhost:9411/api/v2/spans")))
		})
	}

	serverConfig {
		port(5051)

	}

	handlers {

		all(new NcsaRequestLogger(log))

		all { SpanCustomizer spanCustomizer ->
			spanCustomizer.tag("version","v1")
			next()
		}

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
