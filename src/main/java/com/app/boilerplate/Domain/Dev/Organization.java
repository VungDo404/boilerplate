package com.app.boilerplate.Domain.Dev;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.context.annotation.Profile;

import java.io.Serial;

@Profile("!prod")
@Getter
@Setter
@Audited
@AuditTable("organization_history")
@Entity
@Table(name = "organization")
public class Organization {
	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "display_name", length = 50)
	private String displayName;



}
