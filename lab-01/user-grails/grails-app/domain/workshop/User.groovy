package workshop;

import grails.rest.Resource

@Resource(uri = "/users")
public class User {

	String id
	String username

	static constraints = {
		id generator: 'uuid2'
		username nullable: false, blank: false
	}
}
