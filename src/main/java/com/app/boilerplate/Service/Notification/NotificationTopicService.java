package com.app.boilerplate.Service.Notification;

import com.app.boilerplate.Repository.NotificationTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationTopicService {
    private final NotificationTopicRepository notificationTopicRepository;
}