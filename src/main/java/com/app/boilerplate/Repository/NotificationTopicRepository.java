package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Notification.NotificationTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;

public interface NotificationTopicRepository extends JpaRepository<NotificationTopic, Long>,
    RevisionRepository<NotificationTopic, Long, Integer> {
    List<NotificationTopic> findAllBySubscribeByDefaultTrue();
}