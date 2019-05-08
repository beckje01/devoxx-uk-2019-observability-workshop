import brave.sampler.Sampler
import com.zaxxer.hikari.HikariConfig
import ratpack.dropwizard.metrics.DropwizardMetricsModule
import ratpack.dropwizard.metrics.MetricsPrometheusHandler
import ratpack.groovy.template.MarkupTemplateModule
import ratpack.handling.internal.NcsaRequestLogger
import ratpack.hikari.HikariModule
import ratpack.hub.HubDAOService
import ratpack.hub.HubsEndpoint


import org.slf4j.Logger;
import org.slf4j.LoggerFactory
import ratpack.zipkin.ServerTracingModule
import workshop.LoggingContext
import workshop.LoggingResponseHandler
import zipkin2.reporter.AsyncReporter
import zipkin2.reporter.okhttp3.OkHttpSender;

import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack

Logger log = LoggerFactory.getLogger("ratpack.groovy");


ratpack {
	bindings {
		add(LoggingResponseHandler.decorator())
		module MarkupTemplateModule

		//Adds the Hikari Module which will be our db connection pool
		module HikariModule, { HikariConfig c ->
			c.setDriverClassName("com.p6spy.engine.spy.P6SpyDriver")
			c.setJdbcUrl("jdbc:p6spy:h2:mem:dev;INIT=CREATE SCHEMA IF NOT EXISTS DEV")
		}

		module(ServerTracingModule, { config ->
			config
					.serviceName("hub")
					.sampler(Sampler.ALWAYS_SAMPLE)
					.spanReporterV2(AsyncReporter.create(OkHttpSender.create("http://localhost:9411/api/v2/spans")))
		})

		bind(HubDAOService)
		bind(HubsEndpoint)
	}

	handlers {
		all(new NcsaRequestLogger(log))

		get {
			LoggingContext loggingContext = context.get(LoggingContext)
			log.info "Hello World"
			render groovyMarkupTemplate("index.gtpl", title: "My ratpack App: " + loggingContext.loggingId)
		}

		prefix("hubs") {
			all chain(registry.get(HubsEndpoint))
		}


		files { dir "public" }
	}
}
