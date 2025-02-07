import { Component, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from "@angular/router";
import { ImageModule } from "primeng/image";
import { Divider } from "primeng/divider";
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Subject, takeUntil } from "rxjs";

@Component({
    selector: 'app-account',
    imports: [RouterOutlet, ImageModule, RouterLink, Divider, TranslatePipe],
    templateUrl: './account.component.html',
    standalone: true,
    styleUrl: './account.component.scss'
})
export class AccountComponent implements OnDestroy{
    title: string = '';
    message: string = '';
    linkText: string = '';
    linkRoute: string = '';
    private destroy$ = new Subject<void>();

    constructor(private router: Router, public translate: TranslateService) {
        this.router.events
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.updateAuthPage();
            });
    }

    updateAuthPage() {
        const url = this.router.url;
        if (url.includes('/account/login')) {
            this.translate.get(['Authenticate', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['Authenticate'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/account/register';
                });
        } else if (url.includes('/account/register')) {
            this.translate.get(['CreateAccount', 'AlreadyHaveAccount', 'SignIn'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['CreateAccount'];
                    this.message = translations['AlreadyHaveAccount'];
                    this.linkText = translations['SignIn'];
                    this.linkRoute = '/account/login';
                });
        }
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

}
