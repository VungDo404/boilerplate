package com.app.boilerplate.Domain.Authorization;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "acl_class", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"class"}, name = "uk_acl_class")
})
public class AclClass {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
	private Long id;

	@Column(name = "class", nullable = false, length = 100)
	private String clazz;

}
