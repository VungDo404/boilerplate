package com.app.boilerplate.Domain.Notification;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Shared.Common.IdentifiableUserResource;
import com.app.boilerplate.Shared.Notification.NotificationUserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Audited
@AuditTable("notification_user_history")
@Entity
@IdClass(NotificationUserId.class)
@Table(name = "notification_user")
public class NotificationUser implements Serializable, IdentifiableUserResource<String> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false, updatable = false)
    private Notification notification;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Override
    public String getId() {
        return user.getId().toString() + ":" + notification.getId().toString();
    }
}
