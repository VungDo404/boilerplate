package com.app.boilerplate.Service.Notification;

import com.app.boilerplate.Decorator.AclAware.AclAware;
import com.app.boilerplate.Domain.Common.AuditEnversRevision;
import com.app.boilerplate.Domain.Notification.Notification;
import com.app.boilerplate.Domain.Notification.NotificationUser;
import com.app.boilerplate.Mapper.INotificationMapper;
import com.app.boilerplate.Repository.NotificationRepository;
import com.app.boilerplate.Service.Revision.RevisionService;
import com.app.boilerplate.Service.Translation.TranslateService;
import com.app.boilerplate.Shared.Account.Event.RegisterUserEvent;
import com.app.boilerplate.Shared.Notification.Dto.SendNotificationDto;
import com.app.boilerplate.Shared.Notification.Model.NotificationModel;
import com.app.boilerplate.Shared.Notification.Model.NotificationWithReadModel;
import com.app.boilerplate.Util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.RevisionType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> topicEmitters = new ConcurrentHashMap<>();
    private final Map<UUID, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final TopicSubscriptionService topicSubscriptionService;
    private final NotificationUserService notificationUserService;
    private final INotificationMapper notificationMapper;
    private final RevisionService revisionService;
    private final TranslateService translateService;
    private final NotificationTopicService notificationTopicService;

    public void addEmitter(SseEmitter emitter) {
        final var userId = SecurityUtil.getUserId();
        final var topicsSubscription = topicSubscriptionService.findAllSubscriptionsForActiveUser(userId);

        topicsSubscription.forEach(topic -> {
            topicEmitters.computeIfAbsent(topic.getName(), k -> new CopyOnWriteArrayList<>())
                .add(emitter);
        });
        userEmitters.computeIfAbsent(userId, k -> emitter);
    }

    public void removeEmitter(SseEmitter emitter) {
        final var userId = SecurityUtil.getUserId();
        final var topicsSubscription = topicSubscriptionService.findAllSubscriptionsForActiveUser(userId);

        topicsSubscription.forEach(topic -> {
            removeEmitter(topic.getName(), emitter);
        });
        userEmitters.remove(userId);
    }

    public void removeEmitter(String topic, SseEmitter emitter) {
        final CopyOnWriteArrayList<SseEmitter> emittersForTopic = topicEmitters.get(topic);
        if (emittersForTopic != null) {
            emittersForTopic.remove(emitter);
            if (emittersForTopic.isEmpty()) {
                topicEmitters.remove(topic);
            }
        }
    }

    @Transactional(noRollbackFor = {IOException.class, IllegalStateException.class})
    public void sendNotification(String topic, SendNotificationDto notificationDto) {
        final List<SseEmitter> emittersForTopic = topicEmitters.get(topic);
        final var notification = createNotification(notificationDto);
        final var notificationModel = notificationMapper.toNotificationModel(notification);

        notificationModel.setCreatedAt(LocalDateTime.now());
        notificationModel.setIsRead(false);

        final var args = Optional.ofNullable(notification.getMessageArguments())
            .map(s -> s.split(","))
            .map(arr -> Arrays.stream(arr)
                .map(String::trim)
                .toArray())
            .orElse(new Object[0]);
        notificationModel.setTitle(translateService.getMessage(notificationModel.getTitle()));
        notificationModel.setMessage(translateService.getMessage(notificationModel.getMessage(), args));

        final var userIds = topicSubscriptionService.getActiveUserIdsForTopic(notificationDto.getNotificationTopicId());

        Optional.ofNullable(emittersForTopic)
            .ifPresent(list ->
                    list.forEach(emitter -> {
                        try {
                            emitter.send(SseEmitter.event()
                                .name("notification")
                                .data(notificationModel));
                        } catch (IOException | IllegalStateException e) {
                            emitter.completeWithError(e);
                            removeEmitter(topic, emitter);
                            userEmitters.entrySet()
                                .removeIf(entry -> entry.getValue()
                                    .equals(emitter));
                        }
                    })
                      );

        notificationUserService.createNotificationUsersAsync(userIds, notification);
    }

    public List<NotificationModel> getLatestNotifications(int page, int size) {
        final var userId = SecurityUtil.getUserId();

        final var results = revisionService.findRevisionsWithFilters(
            NotificationUser.class,
            Map.of("user.user.id", userId),
            RevisionType.ADD,
            page,
            size);

        final var notificationIds = results.stream()
            .filter(r -> r[0] instanceof NotificationUser)
            .map(r -> ((NotificationUser) r[0]).getNotification()
                .getId())
            .toList();
        final var notifications = notificationUserService.findAllNotificationModelUsers(userId, notificationIds);
        final var notificationMap = notifications.stream()
            .collect(Collectors.toMap(NotificationWithReadModel::getId, Function.identity()));

        return results.stream()
            .filter(r -> r[0] instanceof NotificationUser)
            .map(r -> {
                final var notificationUser = (NotificationUser) r[0];
                final var notification = notificationMap.get(notificationUser.getNotification()
                    .getId());

                if (notification == null) {
                    return null;
                }

                final var args = Optional.ofNullable(notification.getMessageArguments())
                    .map(s -> s.split(","))
                    .map(arr -> Arrays.stream(arr)
                        .map(String::trim)
                        .toArray())
                    .orElse(new Object[0]);
                final var model = notificationMapper.toNotificationModel(notification);
                model.setTitle(translateService.getMessage(model.getTitle()));
                model.setMessage(translateService.getMessage(model.getMessage(), args));
                if (r[1] instanceof AuditEnversRevision rev) {
                    model.setCreatedAt(rev.getTimestamp());
                }

                return model;
            })
            .filter(Objects::nonNull)
            .toList();
    }

    @AclAware
    public Notification createNotification(SendNotificationDto notificationDto) {
        final var notification = notificationMapper.toNotification(notificationDto);
        final var topic = notificationTopicService.getById(notificationDto.getNotificationTopicId());
        notification.setNotificationTopic(topic);
        return notificationRepository.save(notification);
    }

    public void toggleSubscribe(UUID userId, Long topicId) {
        final var ts = topicSubscriptionService.toggleMuteTopic(userId, topicId);
        if(ts.getMuted()) {
           final var emitter = userEmitters.get(userId);
           if(emitter != null) {
               removeEmitter(ts.getNotificationTopic().getName(), emitter);
           }
        }
    }

    @EventListener
    public void addNotificationAfterRegister(RegisterUserEvent event){
        final var user = event.getUser();
        final var notification = notificationRepository.findByTitleOrMessage("notification.welcome.title","notification.welcome.message");

        notificationUserService.createNotificationUsers(List.of(user), notification);
    }
}