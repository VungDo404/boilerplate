import { Component, OnDestroy } from '@angular/core';
import { HeaderSearchComponent } from "./header-search/header-search.component";
import { HeaderAvatarComponent } from "./header-avatar/header-avatar.component";
import { HeaderNotificationComponent } from "./header-notification/header-notification.component";
import { AsyncPipe, NgIf } from "@angular/common";
import { SessionService } from "../../service/session.service";
import { HeaderSignInComponent } from "./header-sign-in/header-sign-in.component";
import { Observable, Subject } from "rxjs";
import { HeaderButtonComponent } from "./header-button/header-button.component";
import { BaseComponent } from "../base.component";

@Component({
    selector: 'header[app-header]',
    imports: [
        HeaderSearchComponent,
        HeaderAvatarComponent,
        HeaderNotificationComponent,
        NgIf,
        HeaderSignInComponent,
        AsyncPipe,
        HeaderButtonComponent
    ],
    templateUrl: './header.component.html',
    standalone: true,
    styleUrl: './header.component.scss'
})
export class HeaderComponent extends BaseComponent {
    isAuthenticated$!: Observable<boolean>;

    constructor(protected sessionService: SessionService) {
        super();
        this.isAuthenticated$ = this.sessionService.isAuthenticated$;
    }
}
