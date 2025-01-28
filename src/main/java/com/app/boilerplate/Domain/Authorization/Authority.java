package com.app.boilerplate.Domain.Authorization;

import com.app.boilerplate.Domain.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "authority")
public class Authority {
	@EmbeddedId
	private AuthorityId id;
	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_AUTHORITY_ON_USER"))
	private User user;
	@ManyToOne
	@MapsId("sidId")
	@JoinColumn(name = "sid_id", nullable = false, foreignKey = @ForeignKey(name = "FK_AUTHORITY_ON_ACL_SID"))
	private AclSid sid;

	@Column(name = "priority", nullable = false, columnDefinition = "INT UNSIGNED")
	private Integer priority;
}
