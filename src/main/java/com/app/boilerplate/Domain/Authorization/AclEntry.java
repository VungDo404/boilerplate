package com.app.boilerplate.Domain.Authorization;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "acl_entry", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"acl_object_identity", "ace_order"}, name = "unique_acl_entry")
})
public class AclEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "acl_object_identity", nullable = false, foreignKey = @ForeignKey(name = "fk_acl_entry_object"))
	private AclObjectIdentity aclObjectIdentity;

	@Column(name = "ace_order", nullable = false)
	private Integer aceOrder;

	@ManyToOne
	@JoinColumn(name = "sid", nullable = false, foreignKey = @ForeignKey(name = "fk_acl_entry_acl"))
	private AclSid sid;

	@Column(name = "mask", nullable = false, columnDefinition = "INTEGER UNSIGNED")
	private Integer mask;

	@Column(name = "granting",nullable = false)
	private Boolean granting;

	@Column(name = "audit_success",nullable = false)
	private Boolean auditSuccess;

	@Column(name = "audit_failure",nullable = false)
	private Boolean auditFailure;

}
