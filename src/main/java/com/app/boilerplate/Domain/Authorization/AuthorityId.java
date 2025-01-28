package com.app.boilerplate.Domain.Authorization;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
@Getter
@Setter
@Embeddable
public class AuthorityId implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
	private UUID userId;

	@Column(name = "sid_id", columnDefinition = "BIGINT UNSIGNED",nullable = false)
	private Long sidId;
}
