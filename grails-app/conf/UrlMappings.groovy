class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

//        "/"(controller: "issue", action: "/index")
        "/"(view: "/index")
        "/about"(view: "/about")
        "500"(view:'/error')
	}
}
