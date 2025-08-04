import { Component, OnInit } from '@angular/core';
import { OverlayBadge } from "primeng/overlaybadge";
import { Avatar } from "primeng/avatar";
import { BaseComponent } from "../../base.component";
import { HeaderNotificationService } from "./header-notification.service";
import { NotificationMenuComponent } from "../../notification-menu/notification-menu.component";
import { ToastService } from "../../../service/toast.service";
import { TranslateService } from "@ngx-translate/core";
import { take } from "rxjs";

@Component({
    selector: 'app-header-notification',
    imports: [
        OverlayBadge,
        Avatar,
        NotificationMenuComponent
    ],
    templateUrl: './header-notification.component.html',
    standalone: true,
    styleUrl: './header-notification.component.scss'
})
export class HeaderNotificationComponent extends BaseComponent implements OnInit {
    constructor(
        private headerNotificationService: HeaderNotificationService,
        private toastService: ToastService,
        private translate: TranslateService) {super();}

    ngOnInit(): void {
        this.headerNotificationService.getNotifications();
        this.headerNotificationService.startEventSource();
    }

    get items() {
        return this.headerNotificationService.notifications;
    }

    set items(newItems) {
        this.headerNotificationService.notifications = newItems;
    }

    get count(): number {
        if (this.items)
            return this.items.reduce((acc, item) => acc + (item.isRead ? 0 : 1), 0);
        return 0;
    }

    onToggleRead(id: number) {
        const cb = () => {
            this.items = this.items.map(item =>
                item.id === id ? { ...item, isRead: !item.isRead } : item
            );
        };
        this.headerNotificationService.toggleReadStatus(id, cb);
    }

    onDelete(id: number) {
        const cb = () => {
            this.translate.get(['Success', 'DeleteNotificationMessage'])
                .pipe(take(1))
                .subscribe(translation => {
                    this.items = this.items.filter(item => item.id !== id);
                    this.toastService.push('success', translation['Success'], translation['DeleteNotificationMessage'], 'bottom-left');
                });
        };
        this.headerNotificationService.deleteNotificationForUser(id, cb);
    }

    onToggleSubscription(topic: NotificationTopic) {
        const cb = () => {
            const message = topic.muted ? 'SubscribeTopicMessage' : 'UnsubscribeTopicMessage';
            this.translate.get([message, 'Success'], { value: topic.name })
                .pipe(take(1))
                .subscribe(translation => {
                    this.items = this.items.map(item =>
                        item.notificationTopicModel?.id === topic.id ?
                            {
                                ...item,
                                notificationTopicModel: { ...topic, muted: !topic.muted }
                            } :
                            item);
                    this.toastService.push('success', translation['Success'], translation[message], 'bottom-left');
                });
        };
        this.headerNotificationService.toggleTopicSubscription(topic.id, cb, topic.muted);
    }
}
