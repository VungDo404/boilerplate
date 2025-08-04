package com.app.boilerplate.Shared.Notification.Dto;

import com.app.boilerplate.Decorator.EnumValidator.ValidEnum;
import com.app.boilerplate.Shared.Notification.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URL;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendNotificationDto {
    @NotNull(message = "{validation.notification.title.required}")
    @Size(min = 3, max = 40, message = "{validation.notification.size}")
    private final String title;
    @NotNull(message = "{validation.notification.message.required}")
    @Size(min = 3, max = 100, message = "{validation.notification.message.size}")
    private final String message;
    @NotNull(message = "{validation.notification.type.required}")
    @ValidEnum(enumClass = NotificationType.class, message = "{validation.notification.type.invalid}")
    private final NotificationType type;
    @NotNull(message = "{validation.notification.topic.required}")
    private final Long notificationTopicId;
    private final URL url;
    private final String messageArguments;
}
