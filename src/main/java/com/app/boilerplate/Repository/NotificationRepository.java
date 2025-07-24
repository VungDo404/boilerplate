package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.Notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, RevisionRepository<Notification,
    Long, Integer> {
}