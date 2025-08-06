package com.app.boilerplate.Service.Notification;

import com.app.boilerplate.Decorator.AclAware.AclAware;
import com.app.boilerplate.Domain.Notification.NotificationTopic;
import com.app.boilerplate.Domain.Notification.TopicSubscription;
import com.app.boilerplate.Repository.TopicSubscriptionRepository;
import com.app.boilerplate.Security.HierarchicalPermission;
import com.app.boilerplate.Shared.Account.Event.RegisterUserEvent;
import com.app.boilerplate.Shared.Notification.TopicSubscriptionId;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class TopicSubscriptionService {
    private final TopicSubscriptionRepository topicSubscriptionRepository;
    private final NotificationTopicService notificationTopicService;

    public List<NotificationTopic> findAllSubscriptionsForActiveUser(UUID userId) {
        return topicSubscriptionRepository.findNotificationTopicsByUserIdAndMutedIsFalse(userId);
    }

    public List<UUID> getActiveUserIdsForTopic(Long notificationTopicId) {
        return topicSubscriptionRepository.findUserIdsByTopicIdAndMutedIsFalse(notificationTopicId);
    }

    public TopicSubscription toggleMuteTopic(UUID userId, Long topicId) {
        final var id = new TopicSubscriptionId(userId, topicId);

        final var subscription = topicSubscriptionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("TopicSubscription not found"));

        subscription.setMuted(!subscription.getMuted());
        return topicSubscriptionRepository.save(subscription);
    }

    @AclAware(permissions = {HierarchicalPermission.MASK_WRITE})
    @EventListener
    public List<TopicSubscription> addSubscriptionAfterRegister(RegisterUserEvent event) {
        final var user = event.getUser();
        final var defaultTopics = notificationTopicService.getAllBySubscribeByDefault();

        final var subscriptions = defaultTopics.stream()
            .map(topic -> new TopicSubscription(user, topic, false))
            .toList();
        return topicSubscriptionRepository.saveAll(subscriptions);
    }
}