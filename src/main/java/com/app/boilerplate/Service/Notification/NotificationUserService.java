package com.app.boilerplate.Service.Notification;

import com.app.boilerplate.Decorator.AclAware.AclAware;
import com.app.boilerplate.Domain.Notification.Notification;
import com.app.boilerplate.Domain.Notification.NotificationUser;
import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Repository.NotificationUserRepository;
import com.app.boilerplate.Security.HierarchicalPermission;
import com.app.boilerplate.Shared.Notification.Model.NotificationWithReadModel;
import com.app.boilerplate.Shared.Notification.NotificationUserId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationUserService {
    private final NotificationUserRepository notificationUserRepository;
    private final EntityManager entityManager;

    @AclAware(permissions = {HierarchicalPermission.MASK_DELETE, HierarchicalPermission.MASK_WRITE})
    @Async
    public CompletableFuture<List<NotificationUser>> createNotificationUsersAsync(List<UUID> userIds, Notification notification) {

        if (notification == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        final var entities = userIds.stream()
            .map(userId -> {
                final var nu = new NotificationUser();
                nu.setUser(entityManager.getReference(User.class, userId));
                nu.setNotification(notification);
                return nu;
            })
            .toList();

        return CompletableFuture.completedFuture(notificationUserRepository.saveAll(entities));
    }

    @AclAware(permissions = {HierarchicalPermission.MASK_DELETE, HierarchicalPermission.MASK_WRITE})
    public List<NotificationUser> createNotificationUsers(List<User> users, Notification notification){
        if (notification == null) {
            return Collections.emptyList();
        }

        final var entities = users.stream()
            .map(user -> {
                final var nu = new NotificationUser();
                nu.setUser(user);
                nu.setNotification(notification);
                return nu;
            })
            .toList();

        return notificationUserRepository.saveAll(entities);
    }

    public List<NotificationWithReadModel> findAllNotificationModelUsers(UUID userId, List<Long> notificationIds) {
        return notificationUserRepository.getNotification(userId, notificationIds);
    }

    public NotificationUser deleteNotificationUser(UUID userId, Long notificationId) {
        NotificationUserId id = new NotificationUserId(userId, notificationId);
        Optional<NotificationUser> existing = notificationUserRepository.findById(id);
        existing.ifPresent(notificationUserRepository::delete);
        return existing.orElse(null);
    }

    public void toggleReadStatus(UUID userId, Long notificationId){
        NotificationUserId id = new NotificationUserId(userId, notificationId);
        NotificationUser notificationUser = notificationUserRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("NotificationUser not found"));

        notificationUser.setIsRead(!notificationUser.getIsRead());
        notificationUserRepository.save(notificationUser);
    }

}