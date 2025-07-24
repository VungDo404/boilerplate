package com.app.boilerplate.Service.Notification;

import com.app.boilerplate.Decorator.AclAware.AclAware;
import com.app.boilerplate.Domain.Notification.Notification;
import com.app.boilerplate.Domain.Notification.NotificationUser;
import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Repository.NotificationUserRepository;
import com.app.boilerplate.Security.HierarchicalPermission;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
}