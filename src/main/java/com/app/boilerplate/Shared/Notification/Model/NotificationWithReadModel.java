package com.app.boilerplate.Shared.Notification.Model;

import com.app.boilerplate.Shared.Notification.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NotificationWithReadModel {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private URL url;
    private NotificationTopicModel notificationTopicModel;
    private LocalDateTime createdAt;
    private Boolean isRead;
    private String messageArguments;

    public NotificationWithReadModel(Long id, String title, String message, NotificationType type, URL url,
        Long topicId, String topicName, Boolean muted, Boolean isRead, String messageArguments) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.url = url;
        this.notificationTopicModel = (topicId != null) ?
            new NotificationTopicModel(topicId, topicName, muted) : null;
        this.isRead = isRead;
        this.messageArguments = messageArguments;
        this.createdAt = null;
    }
}
