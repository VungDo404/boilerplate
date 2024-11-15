package com.app.boilerplate.Domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class LockOut implements Serializable {
    @Serial
	private static final long serialVersionUID = -7139354382957927520L;

	@Audited(withModifiedFlag = true)
    @Column(name = "is_lockout_enabled", columnDefinition = "BIT default 0", nullable = false)
    private Boolean isLockoutEnabled;

	@Audited(withModifiedFlag = true)
    @Column(name = "access_failed_count", columnDefinition = "SMALLINT", nullable = false)
    private int accessFailedCount;

	@Audited(withModifiedFlag = true)
    @Column(name = "lockout_end_date")
    private LocalDateTime lockoutEndDate;
}
