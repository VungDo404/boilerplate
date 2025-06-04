import { Component } from '@angular/core';
import { HeaderSearchComponent } from "./header-search/header-search.component";
import { HeaderAvatarComponent } from "./header-avatar/header-avatar.component";
import { HeaderNotificationComponent } from "./header-notification/header-notification.component";

@Component({
  selector: 'header[app-header]',
    imports: [
        HeaderSearchComponent,
        HeaderAvatarComponent,
        HeaderNotificationComponent
    ],
  templateUrl: './header.component.html',
  standalone: true,
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

}
