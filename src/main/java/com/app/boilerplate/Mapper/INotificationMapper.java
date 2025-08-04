package com.app.boilerplate.Mapper;

import com.app.boilerplate.Domain.Notification.Notification;
import com.app.boilerplate.Domain.Notification.NotificationTopic;
import com.app.boilerplate.Shared.Notification.Dto.SendNotificationDto;
import com.app.boilerplate.Shared.Notification.Model.NotificationModel;
import com.app.boilerplate.Shared.Notification.Model.NotificationTopicModel;
import com.app.boilerplate.Shared.Notification.Model.NotificationWithReadModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface INotificationMapper {
    Notification toNotification(SendNotificationDto notification);

    NotificationModel toNotificationModel(NotificationWithReadModel notification);
    @Mapping(target = "notificationTopicModel", source = "notificationTopic")
    NotificationModel toNotificationModel(Notification notification);

    @Mapping(target = "muted", constant = "false")
    NotificationTopicModel toNotificationTopicModel(NotificationTopic notificationTopic);
}
