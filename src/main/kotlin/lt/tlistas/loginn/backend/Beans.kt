package lt.tlistas.loginn.backend

import lt.tlistas.core.factory.*
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.beans
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.reactive.function.server.RouterFunctions

fun beans() = beans {
	bean<ProjectHandler>()
	bean<Routes>()
	bean<CompanyFactory>()
	bean<CollaboratorFactory>()
	bean<CountryFactory>()
	bean<AddressFactory>()
	bean<CustomerFactory>()
	bean<ProjectFactory>()
	bean<UserFactory>()


	bean("webHandler") {
		RouterFunctions.toWebHandler(ref<Routes>().router())
	}
	bean("messageSource") {
		ReloadableResourceBundleMessageSource().apply {
			setBasename("messages")
			setDefaultEncoding("UTF-8")
		}
	}

	profile("cors") {
		bean("corsFilter") {
			CorsWebFilter { CorsConfiguration().applyPermitDefaultValues() }
		}
	}
}
