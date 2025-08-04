package com.app.boilerplate.Controller.Notification;

import com.app.boilerplate.Service.Notification.NotificationService;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "TopicSubscription")
@RequiredArgsConstructor
@RequestMapping("/topic")
@RestController
public class TopicSubscriptionController {
    private final NotificationService notificationService;

    @PreAuthorize("hasPermission(#userId.toString() + ':' + #id.toString(), '" + PermissionUtil.NOTIFICATION_USER +
        "', '" + PermissionUtil.WRITE + "')")
    @PatchMapping("/{id}/user/{userId}")
    public void toggleTopicSubscription(@PathVariable Long id, @PathVariable UUID userId){
        notificationService.toggleSubscribe(userId, id);
    }
}