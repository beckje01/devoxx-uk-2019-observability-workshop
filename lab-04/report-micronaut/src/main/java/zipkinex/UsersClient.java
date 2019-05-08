package zipkinex;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import workshop.User;

import java.util.List;


@Client("http://127.0.0.1:5051")
public interface UsersClient {

	@Get("/users")
	public List<User> getUsers();

}
