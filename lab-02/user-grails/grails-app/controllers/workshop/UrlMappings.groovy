package workshop

class UrlMappings {

	static mappings = {
		get "/hubs/stats"(controller: "hubStats", action: "index")

		"/$controller/$action?/$id?(.$format)?" {
			constraints {
				// apply constraints here
			}
		}

		"/"(view: "/index")
		"500"(view: '/error')
		"404"(view: '/notFound')
	}
}
