package com.app.boilerplate.Shared.Notification;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class NotificationUserId implements Serializable {
    private UUID user;
    private Long notification;
}
