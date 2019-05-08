package ratpack.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.netty.handler.codec.http.HttpHeaderValues
import ratpack.func.Action
import ratpack.groovy.Groovy
import ratpack.handling.Chain
import ratpack.http.client.HttpClient
import ratpack.http.client.ReceivedResponse
import ratpack.http.client.RequestSpec
import ratpack.zipkin.Zipkin
import workshop.LoggingContext

@Slf4j
@Singleton
class UsersChain implements Action<Chain> {

	private final HttpClient httpClient

	@Inject
	UsersChain(@Zipkin HttpClient httpClient) {
		this.httpClient = httpClient
	}

	@Override
	void execute(Chain chain) throws Exception {
		Groovy.chain(chain) {
			path {
				byMethod {
					get {
						httpClient.requestStream(new URI("http://localhost:8080/users"), { RequestSpec requestSpec ->
							requestSpec.headers({ headers ->
								context.maybeGet(LoggingContext).ifPresent({ loggingContext ->
									headers.set(LoggingContext.LOGGING_HEADER_NAME, loggingContext.loggingId)
								})
							})
						}).then({ streamedResp ->
							streamedResp.forwardTo(context.response)
						})
					}
					post {
						request.getBody().then({ body ->
							httpClient.requestStream(new URI("http://localhost:8080/users"), { RequestSpec requestSpec ->
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
			path(":id") {
				byMethod {
					get {
						httpClient.requestStream(new URI("http://localhost:8080/users/"+context.pathTokens.get("id")), { RequestSpec requestSpec ->
							requestSpec.headers({ headers ->
								context.maybeGet(LoggingContext).ifPresent({ loggingContext ->
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
	}
}
