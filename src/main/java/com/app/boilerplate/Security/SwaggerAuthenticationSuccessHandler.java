package com.app.boilerplate.Security;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Service.Authentication.AuthService;
import com.app.boilerplate.Util.AppConsts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class SwaggerAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final AuthService authService;

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
		final var user = (User) authentication.getPrincipal();
		final var accessToken = authService.createAccessToken(user);
		request.getSession().setAttribute(AppConsts.ACCESS_TOKEN, accessToken);
		response.sendRedirect("/api/swagger-ui/index.html");
	}
}
