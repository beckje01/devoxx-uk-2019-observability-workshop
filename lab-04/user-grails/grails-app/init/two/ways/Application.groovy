package two.ways

import brave.CurrentSpanCustomizer
import brave.SpanCustomizer
import brave.Tracer
import brave.Tracing
import brave.http.HttpServerParser
import brave.http.HttpTracing
import brave.sampler.Sampler
import com.codahale.metrics.MetricRegistry
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.Bean
import zipkin2.reporter.AsyncReporter
import zipkin2.reporter.okhttp3.OkHttpSender
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;


@Slf4j
class Application extends GrailsAutoConfiguration {
	public static void main(String[] args) {
		GrailsApp.run(Application, args)
	}


	@Bean
	public SpanCustomizer getSpanCustomizer(final Tracing tracing) {
		return CurrentSpanCustomizer.create(tracing);
	}

	@Bean
	public Tracer getTracer(final Tracing tracing) {
		return tracing.tracer();
	}

	@Bean
	public Tracing getTracing(final HttpTracing httpTracing) {
		return httpTracing.tracing();
	}

	@Bean
	public HttpTracing getHttpTracing() {
		Tracing tracing = Tracing.newBuilder()
				.localServiceName("user")
				.sampler(Sampler.ALWAYS_SAMPLE)
				.spanReporter(AsyncReporter.create(OkHttpSender.create("http://localhost:9411/api/v2/spans")))
				.build();
		return HttpTracing.newBuilder(tracing)
				.build();
	}

	@Bean
	public MetricRegistry getMetricRegistry(){
		MetricRegistry metricsRegistry = new MetricRegistry()
		CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricsRegistry));

		return metricsRegistry
	}


}
