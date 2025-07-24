package com.app.boilerplate.Shared.Notification.Model;

import com.app.boilerplate.Domain.Notification.NotificationTopic;
import com.app.boilerplate.Shared.Notification.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NotificationModel {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private URL url;
    private NotificationTopic notificationTopic;
    private LocalDateTime createdAt;
}
