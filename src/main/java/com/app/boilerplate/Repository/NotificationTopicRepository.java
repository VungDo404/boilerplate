package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Notification.NotificationTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface NotificationTopicRepository extends JpaRepository<NotificationTopic, Long>,
    RevisionRepository<NotificationTopic, Long, Integer> {
}