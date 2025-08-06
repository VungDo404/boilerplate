package com.app.boilerplate.Config;

import com.app.boilerplate.Util.AppConsts;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OpenAPI30Configuration {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
            .components(new Components()
                .addSecuritySchemes("BearerAuth", createAPIKeyScheme())
                .addParameters("Accept-Language", createAcceptLanguageHeader()))
            .info(new Info().title("Boilerplate REST API")
                .description("API document for Spring boilerplate application.")
                .version("1.0")
                .contact(new Contact().name("James")
                    .email("thanhvungcbt@gmail.com")
                    .url("https://github.com/VungDo404"))
                .license(new License().name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }

    @Bean
    public OpenApiCustomizer globalHeaderCustomizer() {
        return openApi -> openApi.getPaths()
            .values()
            .forEach(pathItem -> pathItem.readOperations()
                .forEach(operation -> operation
                    .addParametersItem(new Parameter().$ref("#/components/parameters/Accept-Language"))));
    }

    @Bean
    WebMvcConfigurer swaggerConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HandlerInterceptor() {
                        @Override
                        public boolean preHandle(
                            HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
                            if (request.getRequestURI()
                                .equals("/swagger-ui/index.html")) {
                                HttpSession session = request.getSession(false);
                                if (session != null) {
                                    String token = (String) session.getAttribute(AppConsts.ACCESS_TOKEN);
                                    if (token != null) {
                                        response.setContentType("text/html");
                                        response.getWriter()
                                            .write("""
                                                	<script>
                                                	window.onload = function() {
                                                		let token = 'Bearer %s';
                                                		const ui = SwaggerUIBundle({
                                                			url: "http://localhost:8080/api/swagger-ui/index.html",
                                                			dom_id: '#swagger-ui',
                                                			deepLinking: true,
                                                			docExpansion: true,
                                                			presets: [
                                                				SwaggerUIBundle.presets.apis,
                                                				SwaggerUIStandalonePreset
                                                			],
                                                			plugins: [
                                                				SwaggerUIBundle.plugins.DownloadUrl
                                                			],
                                                			layout: SwaggerUIStandalonePreset ? "StandaloneLayout" : "BaseLayout",
                                                			defaultModelsExpandDepth: -1,\s
                                                			operationsSorter: "method",
                                                			persistAuthorization: true,
                                                			requestInterceptor(req) {
                                                				req.headers['Authorization'] = token;
                                                				return req;
                                                			}
                                                		});
                                                		window.ui = ui;
                                                	};
                                                </script>
                                                """.formatted(token));
                                    }
                                }
                            }
                            return true;
                        }
                    })
                    .addPathPatterns("/swagger-ui/**");
            }
        };
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


}
