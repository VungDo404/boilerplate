package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Notification.NotificationUser;
import com.app.boilerplate.Shared.Notification.NotificationUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface NotificationUserRepository extends JpaRepository<NotificationUser, NotificationUserId>,
    RevisionRepository<NotificationUser, NotificationUserId, Integer> {
//    @Query("""
//        SELECT new com.app.boilerplate.Shared.Notification.Model.NotificationModel(
//            n.id, n.title, n.message, n.type, n.url, n.notificationTopic,
//        )
//        FROM Notification n
//        JOIN NotificationUser nu ON nu.user.id = :userId
//        JOIN no
//        WHERE ts.muted = false
//
//    """)
//    List<NotificationModel> getLatestNotification(@Param("userId") UUID userId, @Param("limit") Integer limit);
}