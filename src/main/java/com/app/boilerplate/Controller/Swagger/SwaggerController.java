package com.app.boilerplate.Controller.Swagger;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/swagger")
@Controller
public class SwaggerController {
	@GetMapping("/login")
	public String loginPage() {
		return "swagger/login";
	}

}
