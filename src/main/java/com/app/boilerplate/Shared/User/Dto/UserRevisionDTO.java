package com.app.boilerplate.Shared.User.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserRevisionDTO {
	private Integer revisionId;
	private LocalDateTime revisionDate;
	private UUID userId;
}
