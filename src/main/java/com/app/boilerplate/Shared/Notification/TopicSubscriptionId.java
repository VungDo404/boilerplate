package com.app.boilerplate.Shared.Notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicSubscriptionId implements Serializable {
    private UUID user;
    private Long notificationTopic;
}
