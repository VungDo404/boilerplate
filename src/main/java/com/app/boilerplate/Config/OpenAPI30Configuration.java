package com.app.boilerplate.Config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.function.BiConsumer;


@Configuration
public class OpenAPI30Configuration {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
			.components(new Components()
				.addSecuritySchemes("Bearer Authentication", createAPIKeyScheme())
				.addParameters("Accept-Language", createAcceptLanguageHeader()))
			.info(new Info().title("Boilerplate REST API")
				.description("API document for Spring boilerplate application.")
				.version("1.0")
				.contact(new Contact().name("James")
					.email("thanhvungcbt@gmail.com")
					.url("thanhvungcbt@gmail.com"))
				.license(new License().name("License of API")
					.url("API license URL")))
			.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
			.paths(buildPaths());
	}

	private Parameter createAcceptLanguageHeader() {
		return new Parameter()
			.in("header")
			.name("Accept-Language")
			.description("Language preference for the response (e.g., en, vi)")
			.schema(new StringSchema())
			.required(false);
	}

	private SecurityScheme createAPIKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP)
			.bearerFormat("JWT")
			.scheme("bearer");
	}

	private Paths buildPaths() {
		Paths paths = new Paths();

		addPublicPaths(paths, SecurityConfig.POST_PUBLIC_URL, PathItem::setPost);
		addPublicPaths(paths, SecurityConfig.GET_PUBLIC_URL, PathItem::setGet);

		return paths;
	}

	@Bean
	public OpenApiCustomizer globalHeaderCustomizer() {
		return openApi -> openApi.getPaths()
			.values()
			.forEach(pathItem -> {
				pathItem.readOperations()
					.forEach(operation -> {
						operation.addParametersItem(new Parameter().$ref("#/components/parameters/Accept-Language"));
					});
			});
	}

	private void addPublicPaths(Paths paths, String[] urls, BiConsumer<PathItem, Operation> operationSetter) {
		for (String url : urls) {
			if (!url.startsWith("/swagger-ui") && !url.startsWith("/v3/api-docs") &&
				!url.startsWith("/swagger-resources") && !url.startsWith("/webjars")) {
				PathItem pathItem = new PathItem();
				Operation operation =
					new Operation()
						.security(Collections.emptyList());
				operationSetter.accept(pathItem, operation);
				paths.addPathItem(url, pathItem);
			}
		}
	}
}
