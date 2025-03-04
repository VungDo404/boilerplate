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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

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

	@ExceptionHandler(value = {AccessDeniedException.class})
	ResponseEntity<Object> handleAuthenticationException(
			AccessDeniedException exception,
			NativeWebRequest request) {
		return handleException(
				exception,
				HttpStatus.UNAUTHORIZED,
				exception.getMessage(),
				HttpStatus.UNAUTHORIZED.getReasonPhrase(),
				null,
				request,
				null
		);
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

	@ExceptionHandler(value = {BadRequestException.class})
	ResponseEntity<Object> handleNotFoundException(BadRequestException exception, NativeWebRequest request) {
		return handleException(
			exception,
			HttpStatus.BAD_REQUEST,
			exception.getMessage(),
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			null,
			request,
			null
							  );
	}

	@ExceptionHandler(value = {NotFoundException.class})
	ResponseEntity<Object> handleNotFoundException(NotFoundException exception, NativeWebRequest request) {
		return handleException(
			exception,
			resolveResponseStatus(exception),
			exception.getDetailMessageCode(),
			HttpStatus.NOT_FOUND.getReasonPhrase(),
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
				null
		);
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

		final var FIELD_ERRORS = "fieldErrors";
        return handleException(
			exception,
			HttpStatus.valueOf(status.value()),
			"",
			"Validation failed",
			exception.getDetailMessageArguments(),
			request,
			Map.of(FIELD_ERRORS, fieldErrors)
                              );
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


	private ProblemDetail createProblemDetailWithTimestamp(
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
			request
		);
        final var TIME_STAMP = "timestamp";
        problemDetail.setProperty(TIME_STAMP, LocalDateTime.now());
		Optional.ofNullable(properties)
			.ifPresent(problemDetail::setProperties);
		return problemDetail;
	}

	private ResponseEntity<Object> handleException(
		Exception ex,
		HttpStatus status,
		String detailMessageCode,
		String defaultMessage,
		Object[] args,
		WebRequest request,
		Map<String, Object> properties) {

		final var problemDetail = createProblemDetailWithTimestamp(
			ex,
			status,
			detailMessageCode,
			defaultMessage,
			args,
			request,
			properties
		);

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
