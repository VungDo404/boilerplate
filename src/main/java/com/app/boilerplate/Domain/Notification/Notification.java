package com.app.boilerplate.Domain.Notification;

import com.app.boilerplate.Shared.Common.Identifiable;
import com.app.boilerplate.Shared.Notification.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.net.URL;

@Getter
@Setter
@Audited
@AuditTable("notification_history")
@Entity
@Table(name = "notification")
public class Notification implements Serializable, Identifiable<Long> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 40)
    private String title;

    @NotAudited
    @Column(name = "message", nullable = false, updatable = false, length = 100)
    private String message;

    @NotAudited
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    private NotificationType type;

    @NotAudited
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "url", updatable = false)
    private URL url;

    @NotAudited
    @JsonIgnore
    @Column(name = "message_arguments", updatable = false)
    private String messageArguments;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "notification_topic_id")
    private NotificationTopic notificationTopic;

}
