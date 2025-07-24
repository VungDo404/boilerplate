package com.app.boilerplate.Mapper;

import com.app.boilerplate.Domain.Notification.Notification;
import com.app.boilerplate.Shared.Notification.Dto.SendNotificationDto;
import com.app.boilerplate.Shared.Notification.Model.NotificationModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface INotificationMapper {
    Notification toNotification(SendNotificationDto notification);
    NotificationModel toNotificationModel(Notification notification);
}
