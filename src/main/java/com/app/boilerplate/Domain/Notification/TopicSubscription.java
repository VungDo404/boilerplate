package com.app.boilerplate.Domain.Notification;

import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Shared.Common.IdentifiableUserResource;
import com.app.boilerplate.Shared.Notification.TopicSubscriptionId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Audited
@AuditTable("topic_subscription_history")
@Entity
@IdClass(TopicSubscriptionId.class)
@Table(name = "topic_subscription")
public class TopicSubscription implements Serializable, IdentifiableUserResource<String> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_topic_id", updatable = false)
    private NotificationTopic notificationTopic;

    @Column(name = "muted", nullable = false)
    private Boolean muted = false;

    @Override
    public String getId() {
        return user.getId().toString() + ":" + notificationTopic.getId().toString();
    }
}
