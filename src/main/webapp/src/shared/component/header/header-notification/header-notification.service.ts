import { Injectable } from '@angular/core';
import { NotificationService } from "../../../service/http/notification.service";
import { EventSourceMessage } from "@microsoft/fetch-event-source/lib/cjs/parse";

@Injectable({
    providedIn: 'root'
})
export class HeaderNotificationService {
    notifications!: Notification[];
    private controller: AbortController | null = null;

    constructor(private notificationService: NotificationService) { }

    startEventSource() {
        const onmessage = (event: EventSourceMessage) => {
            const result = JSON.parse(event.data) as Notification
            this.notifications.unshift(result);
        }
        this.notificationService.notificationSource(onmessage);
    }

    getNotifications(){
        this.notificationService.getNotification().subscribe({
            next: (response) => {
                this.notifications = response;
            }
        })
    }
}
