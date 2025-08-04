package com.app.boilerplate.Service.Notification;

import com.app.boilerplate.Domain.Notification.NotificationTopic;
import com.app.boilerplate.Domain.Notification.TopicSubscription;
import com.app.boilerplate.Repository.TopicSubscriptionRepository;
import com.app.boilerplate.Shared.Notification.TopicSubscriptionId;
import jakarta.persistence.EntityNotFoundException;
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

    public TopicSubscription toggleMuteTopic(UUID userId, Long topicId) {
        TopicSubscriptionId id = new TopicSubscriptionId(userId, topicId);

        TopicSubscription subscription = topicSubscriptionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("TopicSubscription not found"));

        subscription.setMuted(!subscription.getMuted());
        return topicSubscriptionRepository.save(subscription);
    }
}