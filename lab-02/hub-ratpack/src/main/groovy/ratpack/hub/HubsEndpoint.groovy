package ratpack.hub

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

@Slf4j
@CompileStatic
@Singleton
class HubsEndpoint implements Action<Chain> {

	private final HubDAOService hubDAOService
	private final HttpClient httpClient

	@Inject
	HubsEndpoint(HubDAOService hubDAOService, HttpClient httpClient) {
		this.hubDAOService = hubDAOService
		this.httpClient = httpClient
	}

	@Override
	void execute(Chain chain) throws Exception {
		Groovy.chain(chain) {
			path {
				byMethod {
					get { ObjectMapper objectMapper ->
						//This is the list endpoint
						hubDAOService.getAll().then({ hubs ->
							byContent {
								json {
									response.contentType(HttpHeaderValues.APPLICATION_JSON)
									context.render(objectMapper.writeValueAsString(hubs))
								}
							}
						})
					}
					post { ObjectMapper objectMapper ->
						parse(Hub).flatMap({ Hub hub ->
							return hubDAOService.save(hub)
						}).then({ Hub hub ->
							byContent {
								json {
									response.contentType(HttpHeaderValues.APPLICATION_JSON)
									render(objectMapper.writeValueAsString(hub))
								}
							}
						})
					}
				}
			}


			get("stats") { ObjectMapper objectMapper ->
				response.contentType(HttpHeaderValues.APPLICATION_JSON)
				render(objectMapper.writeValueAsString([hubsConnected: 10, hubsClaimed: 1]))
			}

			get(":hubId") { ObjectMapper objectMapper ->
				hubDAOService.getAll().then({ List<Hub> hubs ->
					def hubId = context.getAllPathTokens().get("hubId")
					log.debug("Looking for hubId: " + hubId)
					byContent {
						json {
							response.contentType(HttpHeaderValues.APPLICATION_JSON)
							def hub = hubs.find { hub -> hub.id == hubId }
							context.render(objectMapper.writeValueAsString(hub))
						}
					}
				})
			}

		}
	}
}
