import brave.sampler.Sampler
import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariConfig
import org.slf4j.MDC
import ratpack.groovy.template.MarkupTemplateModule
import ratpack.handling.internal.NcsaRequestLogger
import ratpack.hikari.HikariModule
import ratpack.hub.HubDAOService
import ratpack.hub.HubsEndpoint

//import zipkin.ZipEndpoint

import org.slf4j.Logger;
import org.slf4j.LoggerFactory
import workshop.LoggingContext
import workshop.LoggingResponseHandler;

import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack

Logger log = LoggerFactory.getLogger("ratpack.groovy");


ratpack {
	bindings {
		add(LoggingResponseHandler.decorator())
		module MarkupTemplateModule

		//Adds the Hikari Module which will be our db connection pool
		module HikariModule, { HikariConfig c ->
			c.addDataSourceProperty("URL", "jdbc:h2:mem:dev;INIT=CREATE SCHEMA IF NOT EXISTS DEV")
			c.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource")
		}

		bind(HubDAOService)
		bind(HubsEndpoint)
//		bind(ZipEndpoint)

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
