package com.app.boilerplate.Controller.Notification;

import com.app.boilerplate.Service.Notification.NotificationService;
import com.app.boilerplate.Service.Notification.NotificationUserService;
import com.app.boilerplate.Shared.Notification.Dto.SendNotificationDto;
import com.app.boilerplate.Shared.Notification.Model.NotificationModel;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Notification")
@RequiredArgsConstructor
@RequestMapping("/notification")
@RestController
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationUserService notificationUserService;

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.NOTIFICATION_TOPIC + "', '" + PermissionUtil.VIEW +
        "')")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        try {
            notificationService.addEmitter(emitter);

            emitter.onCompletion(() -> {
                notificationService.removeEmitter(emitter);
            });
            emitter.onTimeout(() -> {
                notificationService.removeEmitter(emitter);
            });
            emitter.onError(e -> {
                notificationService.removeEmitter(emitter);
            });

        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event()
                    .name("error")
                    .data("Subscription failed: " + e.getMessage()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            notificationService.removeEmitter(emitter);
            emitter.complete();
        }

        return emitter;
    }

    @PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.NOTIFICATION + "', '" + PermissionUtil.CREATE +
        "')")
    @PostMapping("/send/{topic}")
    public String sendNotification(@PathVariable String topic, @RequestBody @Valid SendNotificationDto request) {
        notificationService.sendNotification(topic, request);
        return "Notification sent to topic '" + topic;
    }

    @PreAuthorize("hasPermission('" + PermissionUtil.ROOT + "', '" + PermissionUtil.NOTIFICATION_USER + "', '" + PermissionUtil.VIEW +
        "')")
    @GetMapping
    public List<NotificationModel> getNotifications(@ParameterObject Pageable pageable) {
        return notificationService.getLatestNotifications(pageable.getPageNumber(), pageable.getPageSize());
    }

    @PreAuthorize("hasPermission(#userId.toString() + ':' + #id.toString(), '" + PermissionUtil.NOTIFICATION_USER + "', '" + PermissionUtil.DELETE + "')")
    @DeleteMapping("/{id}/user/{userId}")
    public void deleteById(@PathVariable Long id, @PathVariable UUID userId) {
        notificationUserService.deleteNotificationUser(userId, id);
    }

    @PreAuthorize("hasPermission(#userId.toString() + ':' + #id.toString(), '" + PermissionUtil.NOTIFICATION_USER +
        "', '" + PermissionUtil.WRITE + "')")
    @PatchMapping("/{id}/user/{userId}")
    public void toggleReadStatus(@PathVariable Long id, @PathVariable UUID userId){
        notificationUserService.toggleReadStatus(userId, id);
    }


}