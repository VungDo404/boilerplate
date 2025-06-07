import { Component, Input } from '@angular/core';
import { TranslatePipe } from "@ngx-translate/core";
import { CapitalizeFirstOnlyPipe } from "../../../pipe/capitalize-first-only.pipe";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'app-header-sign-in',
    imports: [
        TranslatePipe,
        CapitalizeFirstOnlyPipe,
        RouterLink
    ],
    templateUrl: './header-sign-in.component.html',
    standalone: true,
    styleUrl: './header-sign-in.component.scss'
})
export class HeaderSignInComponent {
    @Input() routerLink!: string;
}
