package two.ways

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import groovy.util.logging.Slf4j
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
//import org.springframework.cloud.sleuth.zipkin.ZipkinSpanReporter
//import org.springframework.context.annotation.Bean
//import zipkin.Span

@Slf4j
class Application extends GrailsAutoConfiguration {
	public static void main(String[] args) {
		GrailsApp.run(Application, args)
	}

//	@Bean
//	@ConditionalOnProperty(value = "sample.zipkin.enabled", havingValue = "false")
//	public ZipkinSpanReporter spanCollector() {
//		return new ZipkinSpanReporter() {
//			@Override
//			public void report(Span span) {
//				log.info(String.format("Consumer reporting span [%s]", span));
//			}
//		};
//	}
}
