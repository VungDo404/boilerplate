package com.app.boilerplate.Service.Notification;

import com.app.boilerplate.Domain.Notification.NotificationTopic;
import com.app.boilerplate.Exception.NotFoundException;
import com.app.boilerplate.Repository.NotificationTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationTopicService {
    private final NotificationTopicRepository notificationTopicRepository;

    public NotificationTopic getById(Long id) {
        return notificationTopicRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Notification topic not found", "error.notification-topic" +
                ".notfound"));
    }


}