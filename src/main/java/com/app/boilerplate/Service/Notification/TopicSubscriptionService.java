package com.app.boilerplate.Service.Notification;

import com.app.boilerplate.Domain.Notification.NotificationTopic;
import com.app.boilerplate.Repository.TopicSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class TopicSubscriptionService {
    private final TopicSubscriptionRepository topicSubscriptionRepository;

    public List<NotificationTopic> findAllSubscriptionsForActiveUser(UUID userId) {
        return topicSubscriptionRepository.findNotificationTopicsByUserIdAndMutedIsFalse(userId);
    }

    public List<UUID> getActiveUserIdsForTopic(Long notificationTopicId){
        return topicSubscriptionRepository.findUserIdsByTopicIdAndMutedIsFalse(notificationTopicId);
    }
}