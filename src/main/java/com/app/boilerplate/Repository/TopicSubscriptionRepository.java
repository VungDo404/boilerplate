package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Notification.NotificationTopic;
import com.app.boilerplate.Domain.Notification.TopicSubscription;
import com.app.boilerplate.Shared.Notification.TopicSubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TopicSubscriptionRepository extends JpaRepository<TopicSubscription, TopicSubscriptionId>,
    RevisionRepository<TopicSubscription, TopicSubscriptionId, Integer> {
    @Query("""
        SELECT ts.notificationTopic
        FROM TopicSubscription ts WHERE ts.user.id = :userId AND ts.muted = false
    """)
    List<NotificationTopic> findNotificationTopicsByUserIdAndMutedIsFalse(@Param("userId") UUID userId);

    @Query("""
            SELECT ts.user.id
            FROM TopicSubscription ts
            WHERE ts.notificationTopic.id = :topicId AND ts.muted = false
        """)
    List<UUID> findUserIdsByTopicIdAndMutedIsFalse(@Param("topicId") Long topicId);
}