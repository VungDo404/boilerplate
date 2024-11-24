window.onload = function() {
	window.ui = SwaggerUIBundle({
		url: "/v3/api-docs",
		dom_id: '#swagger-ui',
		deepLinking: true,
		presets: [
			SwaggerUIBundle.presets.apis,
			SwaggerUIStandalonePreset
		],
		plugins: [
			SwaggerUIBundle.plugins.DownloadUrl
		],
		requestInterceptor: (req) => {
			const token = localStorage.getItem('swagger_access_token');
			if (token) {
				req.headers.Authorization = `Bearer ${token}`;
			}
			return req;
		}
	});
};
