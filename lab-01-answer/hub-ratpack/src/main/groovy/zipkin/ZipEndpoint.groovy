package zipkin

import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import ratpack.func.Action
import ratpack.groovy.Groovy
import ratpack.handling.Chain
import ratpack.http.client.HttpClient
import ratpack.http.client.ReceivedResponse
import ratpack.zipkin.Zipkin

@Slf4j
@CompileStatic
@Singleton
class ZipEndpoint implements Action<Chain> {

	private final HttpClient httpClient

	@Inject
	ZipEndpoint(@Zipkin HttpClient httpClient) {
		this.httpClient = httpClient
	}

	@Override
	void execute(Chain chain) throws Exception {
		Groovy.chain(chain) {
			get('hello') {
				render httpClient.get(new URI("http://127.0.0.1:8081/hello")).map { ReceivedResponse response ->
					return response.body.text
				}
			}

			get("other") {
				render httpClient.get(new URI("http://127.0.0.1:8081/other")).map { ReceivedResponse response ->
					return response.body.text
				}
			}
		}
	}
}
