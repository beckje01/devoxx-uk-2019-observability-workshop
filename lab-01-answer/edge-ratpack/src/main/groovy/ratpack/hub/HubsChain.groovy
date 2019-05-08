package ratpack.hub

import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.util.logging.Slf4j
import ratpack.func.Action
import ratpack.groovy.Groovy
import ratpack.handling.Chain
import ratpack.http.client.HttpClient
import ratpack.http.client.RequestSpec
import workshop.LoggingContext

import static net.logstash.logback.argument.StructuredArguments.*


@Slf4j
@Singleton
class HubsChain implements Action<Chain> {

	private final HttpClient httpClient

	@Inject
	HubsChain(HttpClient httpClient) {
		this.httpClient = httpClient
	}

	@Override
	void execute(Chain chain) throws Exception {
		Groovy.chain(chain) {
			path {
				byMethod {
					get {
						log.debug("Proxy call to hub service")
						log.debug("{}",entries(["method":"get","call":"hubs"]))
						//stream response from hub service
						httpClient.requestStream(new URI("http://localhost:5050/hubs"), { RequestSpec requestSpec ->
							requestSpec.headers({ headers ->
								context.maybeGet(LoggingContext).ifPresent({ loggingContext ->
									log.debug("Adding Logging Context value to headers")
									headers.set(LoggingContext.LOGGING_HEADER_NAME, loggingContext.loggingId)
								})
							})
						}).then({ streamedResp ->
							streamedResp.forwardTo(context.response)
						})
					}
					post {
						//forward to hub service
						request.getBody().then({ body ->
							httpClient.requestStream(new URI("http://localhost:5050/hubs"), { RequestSpec requestSpec ->
								requestSpec.post()


								requestSpec.headers({ headers ->
									request.headers.asMultiValueMap().forEach({ String key, String value ->
										headers.add(key, value)
									})

									context.maybeGet(LoggingContext).ifPresent({ loggingContext ->
										log.debug("Adding Logging Context value to headers")
										headers.set(LoggingContext.LOGGING_HEADER_NAME, loggingContext.loggingId)
									})


								})
								requestSpec.body.bytes(body.bytes)
							}).then({ streamedResp ->
								streamedResp.forwardTo(context.response)
							})

						})


					}
				}
			}



			get("stats") {
				httpClient.requestStream(new URI("http://localhost:5050/hubs/stats"), { RequestSpec requestSpec ->
					requestSpec.headers({ headers ->
						context.maybeGet(LoggingContext).ifPresent({ loggingContext ->
							log.debug("Adding Logging Context value to headers")
							headers.set(LoggingContext.LOGGING_HEADER_NAME, loggingContext.loggingId)
						})
					})
				}).then({ streamedResp ->
					streamedResp.forwardTo(context.response)
				})
			}

			get(":hubId") {
				log.debug(context.pathTokens.get("hubId"))
				httpClient.requestStream(new URI("http://localhost:5050/hubs/"+context.pathTokens.get("hubId")), { RequestSpec requestSpec ->
					requestSpec.headers({ headers ->
						context.maybeGet(LoggingContext).ifPresent({ loggingContext ->
							log.debug("Adding Logging Context value to headers")
							headers.set(LoggingContext.LOGGING_HEADER_NAME, loggingContext.loggingId)
						})
					})
				}).then({ streamedResp ->
					streamedResp.forwardTo(context.response)
				})
			}
		}
	}
}
