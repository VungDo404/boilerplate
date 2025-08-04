import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { DatePipe, NgIf } from "@angular/common";
import { RouterLink } from "@angular/router";
import { MenuItem } from "primeng/api";
import { Menu } from "primeng/menu";
import { LangChangeEvent, TranslateService } from "@ngx-translate/core";
import { BaseComponent } from "../../base.component";
import { takeUntil } from "rxjs";

@Component({
    selector: 'app-notification-menu-item',
    imports: [
        DatePipe,
        NgIf,
        RouterLink,
        Menu
    ],
    templateUrl: './notification-menu-item.component.html',
    standalone: true,
    styleUrl: './notification-menu-item.component.scss'
})
export class NotificationMenuItemComponent extends BaseComponent implements OnInit, OnChanges {
    @Input() item!: Notification;
    @Output() onDelete = new EventEmitter<number>();
    @Output() onToggleRead = new EventEmitter<number>();
    @Output() onToggleSubscription = new EventEmitter<NotificationTopic>();

    hoveringMoreIcon = false;
    menuVisible = false;
    action!: MenuItem[] | undefined;

    constructor(private translate: TranslateService) {super();}

    ngOnInit(): void {
        this.loadAction();
        this.translate.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.loadAction();
            });
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['item']) {
            this.loadAction();
        }
    }

    read() {
        if (!this.item?.isRead)
            this.toggleRead()
    }

    private delete() {
        this.onDelete.emit(this.item.id);
    }

    onActionMenu(event: MouseEvent, menu: Menu) {
        event.stopPropagation();
        this.menuVisible = !this.menuVisible;
        menu.toggle(event);
    }

    private toggleRead() {
        this.onToggleRead.emit(this.item.id);
    }

    private toggleTopic(){
        if(this.item.notificationTopicModel)
            this.onToggleSubscription.emit(this.item.notificationTopicModel);
    }

    private loadAction() {
        const markStatus = this.item?.isRead ? 'MarkUnread' : 'MarkRead';
        const keys = [markStatus, 'DeleteNotification'];
        const actions: MenuItem[] = [];

        if (this.item?.notificationTopicModel) {
            const subscribe = this.item.notificationTopicModel.muted ? 'TurnOnNotifications' : 'TurnOffNotifications';
            keys.push(subscribe);
        }

        this.translate.get(keys).pipe(takeUntil(this.destroy$)).subscribe(translations => {
            actions.push({
                label: translations[markStatus],
                icon: this.item?.isRead ? 'pi pi-eye-slash' : 'pi pi-eye',
                command: this.toggleRead.bind(this)
            });

            actions.push({
                label: translations['DeleteNotification'],
                icon: 'pi pi-trash',
                command: this.delete.bind(this)
            });
            if (this.item?.notificationTopicModel) {
                const isMuted = this.item.notificationTopicModel.muted;
                const subscribeKey = isMuted ? 'TurnOnNotifications' : 'TurnOffNotifications';
                actions.push({
                    label: translations[subscribeKey],
                    icon: isMuted ? 'pi pi-bell' : 'pi pi-bell-slash',
                    command: this.toggleTopic.bind(this)
                });
            }

            this.action = actions;
        });
    }

}
