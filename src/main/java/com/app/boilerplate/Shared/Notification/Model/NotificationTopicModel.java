package com.app.boilerplate.Shared.Notification.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NotificationTopicModel {
    private Long id;
    private String name;
    private Boolean muted;
}
