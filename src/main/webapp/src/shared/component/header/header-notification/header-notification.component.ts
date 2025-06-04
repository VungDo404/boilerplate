import { Component } from '@angular/core';
import { OverlayBadge } from "primeng/overlaybadge";
import { Avatar } from "primeng/avatar";

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
export class HeaderNotificationComponent {

}
