package com.app.boilerplate.Exception;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestControllerAdvice
public class ControllerException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {LockedException.class})
	ResponseEntity<Object> handleLockedException(
		LockedException exception,
		NativeWebRequest request) {
		return handleException(
			exception,
			HttpStatus.LOCKED,
			exception.getDetailMessageCode(),
			HttpStatus.LOCKED.getReasonPhrase(),
			exception.getDetailMessageArguments(),
			request,
			null
		);
	}

	@ExceptionHandler(value = {RateLimitExceededException.class})
	ResponseEntity<Object> handleRateLimitExceededException(
		RateLimitExceededException exception,
		NativeWebRequest request) {
        return handleException(
            exception,
            HttpStatus.TOO_MANY_REQUESTS,
			exception.getDetailMessageCode(),
            HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
            null,
            request,
            null);
	}

	@ExceptionHandler(value = {DisabledException.class, AccountExpiredException.class})
	ResponseEntity<Object> handleLockedException(
		Exception exception,
		NativeWebRequest request) {
		return handleException(
			exception,
			HttpStatus.FORBIDDEN,
			exception.getMessage(),
			HttpStatus.FORBIDDEN.getReasonPhrase(),
			null,
			request,
			null
		);
	}

	@ExceptionHandler(value = {InvalidBearerTokenException.class})
	ResponseEntity<Object> handleInvalidBearerTokenException(
		InvalidBearerTokenException exception,
		NativeWebRequest request) {
		return handleException(
			exception,
			HttpStatus.UNAUTHORIZED,
			"error.auth.permission.denied",
			HttpStatus.UNAUTHORIZED.getReasonPhrase(),
			null,
			request,
			null);
	}

	@ExceptionHandler(value = {AuthorizationDeniedException.class})
	ResponseEntity<Object> handleAuthorizationDeniedException(
		AuthorizationDeniedException exception,
		NativeWebRequest request) {
		return handleException(
			exception,
			HttpStatus.FORBIDDEN,
			"error.auth.permission.denied",
			HttpStatus.FORBIDDEN.getReasonPhrase(),
			null,
			request,
			null);
	}

	@ExceptionHandler(value = {AccessDeniedException.class})
	ResponseEntity<Object> handleAccessDeniedException(
			AccessDeniedException exception,
			NativeWebRequest request) {
		return handleException(
				exception,
				HttpStatus.FORBIDDEN,
				exception.getMessage(),
				HttpStatus.FORBIDDEN.getReasonPhrase(),
				null,
				request,
				null
		);
	}

	@ExceptionHandler(value = {InvalidVerificationCodeException.class})
	ResponseEntity<Object> handleInvalidVerificationCodeException(
		InvalidVerificationCodeException exception,
		NativeWebRequest request) {
		return handleException(
			exception,
			HttpStatus.UNAUTHORIZED,
			exception.getMessage(),
			HttpStatus.UNAUTHORIZED.getReasonPhrase(),
			null,
			request,
			null);
	}

	@ExceptionHandler(value = {BadCredentialsException.class, UsernameNotFoundException.class})
	ResponseEntity<Object> handleUsernameNotFoundException(
		Exception exception,
		NativeWebRequest request) {
		return handleException(
			exception,
			HttpStatus.FORBIDDEN,
			"error.auth.credential",
			"Bad credentials",
			null,
			request,
			null
		);
	}

	@ExceptionHandler(value = {BadJwtException.class})
	ResponseEntity<Object> handleBadJwtException(
		BadJwtException exception,
		NativeWebRequest request) {
		return handleException(
			exception,
			HttpStatus.UNAUTHORIZED,
			"error.auth.token.invalid",
			HttpStatus.UNAUTHORIZED.getReasonPhrase(),
			null,
			request,
			null);
	}

	@ExceptionHandler(value = {BadRequestException.class})
	ResponseEntity<Object> handleNotFoundException(BadRequestException exception, NativeWebRequest request) {
		return handleException(
			exception,
			HttpStatus.BAD_REQUEST,
			exception.getMessage(),
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			null,
			request,
			null);
	}

	@ExceptionHandler(value = {NotFoundException.class})
	ResponseEntity<Object> handleNotFoundException(NotFoundException exception, NativeWebRequest request) {
		final var statusCode = HttpStatus.valueOf(exception.getStatusCode().value());
		return handleException(
			exception,
			statusCode,
			exception.getDetailMessageCode(),
			statusCode.getReasonPhrase(),
			exception.getDetailMessageArguments(),
			request,
			null
		);
	}

	@ExceptionHandler(value = {AlreadyExistsException.class})
	ResponseEntity<Object> handleNotFoundException(AlreadyExistsException exception, NativeWebRequest request) {
		return handleException(
			exception,
			HttpStatus.CONFLICT,
			exception.getMessage(),
			HttpStatus.CONFLICT.getReasonPhrase(),
			null,
			request,
			null);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException exception,
		@NonNull HttpHeaders headers,
		HttpStatusCode status,
		@NonNull WebRequest request) {

		final var fieldErrors = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(f -> FieldErrorVM.builder()
				.objectName(f.getObjectName()
					.replaceFirst("(?i)Dto$", ""))
				.message(StringUtils.isNotBlank(f.getDefaultMessage()) ? f.getDefaultMessage() : f.getCode())
				.field(f.getField())
				.build())
			.toList();

        return handleException(
			exception,
			HttpStatus.valueOf(status.value()),
			"",
			"Validation failed",
			exception.getDetailMessageArguments(),
			request,
			Map.of("fieldErrors", fieldErrors));
	}

	@ExceptionHandler(value = {IOException.class})
	ResponseEntity<Object> handleIOException(IOException exception, NativeWebRequest request) {

		return handleException(
			exception,
			HttpStatus.INTERNAL_SERVER_ERROR,
			exception.getMessage(),
			exception.getMessage(),
			null,
			request,
			null);
	}

	@Override
	protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
		@NonNull MaxUploadSizeExceededException exception,
		@NonNull HttpHeaders headers,
		HttpStatusCode status,
		@NonNull WebRequest request) {
		final var statusCode = HttpStatus.valueOf(status.value());
		return handleException(
			exception,
			statusCode,
			"error.file.exceed",
			exception.getMessage(),
			new Object[]{exception.getMaxUploadSize()},
			request,
			Map.of("maxSizeInBytes", exception.getMaxUploadSize()));
	}

	@ExceptionHandler(value = {UnsupportedMediaTypeStatusException.class})
	protected ResponseEntity<Object> handleUnsupportedMediaTypeStatusException(UnsupportedMediaTypeStatusException exception, NativeWebRequest request) {
		final var statusCode = HttpStatus.valueOf(exception.getStatusCode().value());
		final var arg = exception.getSupportedMediaTypes()
			.stream()
			.map(MediaType::toString)
			.collect(Collectors.joining(", "));
		final var supportedMediaType = exception.getSupportedMediaTypes()
			.stream()
			.map(mediaType ->
				Map.of("type", mediaType.getType(),
					"subtype", mediaType.getSubtype())
				)
			.toList();
		return handleException(
			exception,
			statusCode,
			"error.file.unsupported",
			exception.getMessage(),
			new Object[]{arg},
			request,
			Map.of("supportedMediaType", supportedMediaType));
	}

	@ExceptionHandler(value = {RuntimeException.class, Exception.class})
	ResponseEntity<Object> handleRuntimeException(Exception exception, NativeWebRequest request) {
		return handleException(
			exception,
			resolveResponseStatus(exception),
			exception.getMessage(),
			exception.getMessage(),
			null,
			request,
			null
		);
	}

	private ResponseEntity<Object> handleException(
		Exception ex,
		HttpStatus status,
		String detailMessageCode,
		String defaultMessage,
		Object[] args,
		WebRequest request,
		Map<String, Object> properties) {

		final var problemDetail = createProblemDetail(
			ex,
			status,
			defaultMessage,
			detailMessageCode,
			args,
			request);

		if(properties != null) {
			properties.forEach(problemDetail::setProperty);
		}
		problemDetail.setProperty("timestamp", Instant.now());

		return ResponseEntity
			.status(problemDetail.getStatus())
			.body(problemDetail);
	}

	private HttpStatus resolveResponseStatus(Exception exception) {
		if (exception instanceof ResponseStatusException responseStatusException) {
			return responseStatusException.getStatusCode()
				.is5xxServerError()
				? HttpStatus.INTERNAL_SERVER_ERROR
				: HttpStatus.valueOf(responseStatusException.getStatusCode()
				.value());
		}

		ResponseStatus responseStatus = AnnotationUtils.findAnnotation(
			exception.getClass(),
			ResponseStatus.class
		);

		return responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
	}

}
