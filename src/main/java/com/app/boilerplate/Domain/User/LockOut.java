package com.app.boilerplate.Domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class LockOut implements Serializable {
    private static final long serialVersionUID = -7139354382957927520L;

    @Column(name = "is_lockout_enabled", columnDefinition = "BIT default 0")
    private Boolean isLockoutEnabled;

    @Column(name = "access_failed_count", columnDefinition = "SMALLINT")
    private int accessFailedCount;

    @Column(name = "lockout_end_date")
    private LocalDateTime lockoutEndDate;
}
