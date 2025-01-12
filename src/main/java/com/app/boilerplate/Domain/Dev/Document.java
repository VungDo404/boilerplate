package com.app.boilerplate.Domain.Dev;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;
import org.springframework.context.annotation.Profile;

import java.io.Serial;
import java.time.LocalDateTime;

@Profile("!prod")
@Getter
@Setter
@Audited
@AuditTable("document_history")
@Entity
@Table(name = "document")
public class Document {
	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "description", length = 100)
	private String description;

	@Column(name = "title", length = 50)
	private String title;

	@Column(name = "is_approved", nullable = false, columnDefinition = "BIT default 0")
	@JdbcTypeCode(SqlTypes.BIT)
	private boolean isApproved;

	@Column(name = "expiration")
	private LocalDateTime expiration;

}
