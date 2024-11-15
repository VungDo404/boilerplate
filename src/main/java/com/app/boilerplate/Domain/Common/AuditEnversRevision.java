package com.app.boilerplate.Domain.Common;

import com.app.boilerplate.Util.ApplicationRevisionListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "audit_envers_revision")
@RevisionEntity(ApplicationRevisionListener.class)
public class AuditEnversRevision implements Serializable {
	@Serial
	private static final long serialVersionUID = 2769036020432917162L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@RevisionNumber
	private int id;

	@RevisionTimestamp
	@Column
	private LocalDateTime timestamp;

	@Column(name = "user_id")
	private UUID userId;

	@ElementCollection
	@JoinTable(name = "audit_envers_modified_entity",
		joinColumns = @JoinColumn(name = "revision_id"))
	@Column(name = "entity_name")
	@ModifiedEntityNames
	private Set<String> entityName = new HashSet<>();


}
