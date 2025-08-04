package com.app.boilerplate.Shared.Notification.Model;

import com.app.boilerplate.Shared.Notification.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NotificationModel {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private URL url;
    private NotificationTopicModel notificationTopicModel;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
