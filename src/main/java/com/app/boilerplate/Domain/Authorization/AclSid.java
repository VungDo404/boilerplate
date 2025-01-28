package com.app.boilerplate.Domain.Authorization;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "acl_sid", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"sid", "principal"}, name = "unique_acl_sid")
})
public class AclSid {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
	private Long id;

	@Column(name = "principal", nullable = false)
	private boolean principal;

	@Column(name = "sid", nullable = false, length = 100)
	private String sid;
}
