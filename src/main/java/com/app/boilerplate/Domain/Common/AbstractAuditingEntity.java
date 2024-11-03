package com.app.boilerplate.Domain.Common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(value = { "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate" },
	allowGetters = true)
@EntityListeners(AuditingEntityListener.class)
public class AbstractAuditingEntity implements Serializable {
	private static final long serialVersionUID = 2448836956758854097L;

	@CreatedBy
	@Column(name = "created_by", updatable = false, nullable = false)
	private String createdBy;

	@CreatedDate
	@Column(name = "created_date", updatable = false)
	private LocalDateTime createdDate;

	@LastModifiedBy
	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "last_modified_date")
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

}
