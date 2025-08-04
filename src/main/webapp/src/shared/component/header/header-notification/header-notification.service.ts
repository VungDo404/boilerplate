import { Injectable } from '@angular/core';
import { NotificationService } from "../../../service/http/notification.service";
import { TopicService } from "../../../service/http/topic.service";
import { SessionService } from "../../../service/session.service";
import { Subscription } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class HeaderNotificationService {
    notifications!: Notification[];
    private sseSubscription?: Subscription;

    constructor(
        private notificationService: NotificationService,
        private topicService: TopicService,
        private sessionService: SessionService
    ) { }

    startEventSource() {
        this.sseSubscription = this.notificationService.notificationSource().subscribe(event => {
            if (event.type === 'error') {
                const errorEvent = event as ErrorEvent;
                console.error(errorEvent.error, errorEvent.message);
            } else {
                const messageEvent = event as MessageEvent<string>;
                const no: Notification = JSON.parse(messageEvent.data);
                this.notifications.unshift(no);
                if (this.notifications.length > 10) {
                    this.notifications.length = 10;
                }
            }
        });
    }

    getNotifications() {
        this.notificationService.getNotification().subscribe({
            next: (response) => {
                this.notifications = response;
            }
        });
    }

    toggleReadStatus(id: number, cb: () => void) {
        this.notificationService.toggleRead(id, this.sessionService.id).subscribe({
            next: cb
        });
    }

    deleteNotificationForUser(id: number, cb: () => void) {
        this.notificationService.deleteNotification(id, this.sessionService.id).subscribe({
            next: cb
        });
    }

    toggleTopicSubscription(id: number, cb: () => void, isSubscribe: boolean) {
        this.topicService.toggleTopicSubscription(id, this.sessionService.id).subscribe({
            next: () => {
                if(isSubscribe){
                    this.stopEventSource();
                    this.startEventSource();
                }
                cb();
            }
        })
    }

    private stopEventSource() {
        if (this.sseSubscription) {
            this.sseSubscription.unsubscribe();
            this.sseSubscription = undefined;
        }
    }
}
