import { Component, OnInit } from '@angular/core';
import { OverlayBadge } from "primeng/overlaybadge";
import { Avatar } from "primeng/avatar";
import { BaseComponent } from "../../base.component";
import { HeaderNotificationService } from "./header-notification.service";

@Component({
    selector: 'app-header-notification',
    imports: [
        OverlayBadge,
        Avatar
    ],
    templateUrl: './header-notification.component.html',
    standalone: true,
    styleUrl: './header-notification.component.scss'
})
export class HeaderNotificationComponent extends BaseComponent implements OnInit {

    constructor(private headerNotificationService: HeaderNotificationService) {super();}

    ngOnInit(): void {
        this.headerNotificationService.getNotifications();
        this.headerNotificationService.startEventSource();
    }


}
