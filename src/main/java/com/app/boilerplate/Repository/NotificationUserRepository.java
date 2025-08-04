package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Notification.NotificationUser;
import com.app.boilerplate.Shared.Notification.Model.NotificationWithReadModel;
import com.app.boilerplate.Shared.Notification.NotificationUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationUserRepository extends JpaRepository<NotificationUser, NotificationUserId>,
    RevisionRepository<NotificationUser, NotificationUserId, Integer> {
    @Query("""
        SELECT new com.app.boilerplate.Shared.Notification.Model.NotificationWithReadModel(
            n.id,
            n.title,
            n.message,
            n.type,
            n.url,
            t.id,
            t.name,
            ts.muted,
            nu.isRead,
            n.messageArguments
        )
        FROM NotificationUser nu
        JOIN nu.notification n
        LEFT JOIN n.notificationTopic t
        LEFT JOIN TopicSubscription ts ON ts.notificationTopic = t AND ts.user = nu.user
        WHERE nu.user.id = :userId AND n.id IN :notificationIds
    """)
    List<NotificationWithReadModel> getNotification(
        @Param("userId") UUID userId,
        @Param("notificationIds") List<Long> notificationIds);
}