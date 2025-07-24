package com.app.boilerplate.Domain.Notification;

import com.app.boilerplate.Shared.Common.Identifiable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Audited
@AuditTable("notification_topic_history")
@Entity
@Table(name = "notification_topic")
public class NotificationTopic implements Serializable, Identifiable<Long> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @NotAudited
    @Column(name = "name", nullable = false, unique = true, length = 10, updatable = false)
    private String name;
}
