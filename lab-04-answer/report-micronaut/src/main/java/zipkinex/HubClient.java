package zipkinex;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import workshop.Hub;

import java.util.List;

@Client("http://127.0.0.1:5050")
public interface HubClient {

    @Get("/hubs/stats")
    public String getStats();


	@Get("/hubs")
	public List<Hub> getHubs();

}
