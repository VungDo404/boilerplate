import { ChangeDetectorRef, Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { NgForOf, NgIf } from "@angular/common";
import { MenuDirective } from "../../directive/menu.directive";
import { TranslatePipe } from "@ngx-translate/core";
import { NotificationMenuItemComponent } from "./notification-menu-item/notification-menu-item.component";

@Component({
    selector: 'app-notification-menu',
    imports: [
        NgIf,
        MenuDirective,
        TranslatePipe,
        NgForOf,
        NotificationMenuItemComponent
    ],
    templateUrl: './notification-menu.component.html',
    standalone: true,
    styleUrl: './notification-menu.component.scss'
})
export class NotificationMenuComponent {
    @ViewChild('menuEl', { static: false }) menuDirective!: MenuDirective;
    @Input() items!: Notification[];
    @Output() onDelete = new EventEmitter<number>();
    @Output() onToggleRead = new EventEmitter<number>();
    @Output() onToggleSubscription = new EventEmitter<NotificationTopic>();
    protected isVisible = false;
    position = { top: 0, left: 0 };

    constructor(private changeDetectorRef: ChangeDetectorRef) {}

    toggle(event: Event) {
        event.stopPropagation();
        if (this.isVisible) {
            this.onHide()
        } else {
            this.isVisible = true;
            this.changeDetectorRef.detectChanges();
            this.menuDirective.show(event.currentTarget as HTMLElement);
        }
    }

    onPositionChange(pos: { top: number; left: number }) {
        this.position = pos;
    }

    onHide() {
        this.isVisible = false;
    }

    handleToggleRead(id: number) {
        this.onToggleRead.emit(id);
    }

    handleDelete(id: number) {
        this.onDelete.emit(id);
    }

    handleToggleSubscription(topic: NotificationTopic){
        this.onToggleSubscription.emit(topic);
    }
}
