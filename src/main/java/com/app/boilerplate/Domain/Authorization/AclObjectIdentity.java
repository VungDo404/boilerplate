package com.app.boilerplate.Domain.Authorization;

import com.app.boilerplate.Shared.Authorization.Model.AclObjectIdentityWithLevelModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@SqlResultSetMapping(
	name = "AclObjectIdentityWithLevelMapping",
	classes = @ConstructorResult(
		targetClass = AclObjectIdentityWithLevelModel.class,
		columns = {
			@ColumnResult(name = "id", type = Long.class),
			@ColumnResult(name = "object_id_class", type = AclClass.class),
			@ColumnResult(name = "object_id_identity", type = String.class),
			@ColumnResult(name = "parent_object", type = AclObjectIdentity.class),
			@ColumnResult(name = "level", type = Integer.class)
		}
	)
)
@Table(name = "acl_object_identity", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"object_id_class", "object_id_identity"}, name = "uk_acl_object_identity")
})
public class AclObjectIdentity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "object_id_class", nullable = false, foreignKey = @ForeignKey(name = "fk_acl_object_identity_class"))
	private AclClass objectIdClass;

	@Column(name = "object_id_identity", nullable = false, length = 100)
	private String objectIdIdentity;

	@ManyToOne
	@JoinColumn(name = "parent_object", foreignKey = @ForeignKey(name = "fk_acl_object_identity_parent"))
	private AclObjectIdentity parentObject;

	@ManyToOne
	@JoinColumn(name = "owner_sid", foreignKey = @ForeignKey(name = "fk_acl_object_identity_owner"))
	private AclSid ownerSid;

	@Column(name = "entries_inheriting", nullable = false)
	private boolean entriesInheriting;

}
