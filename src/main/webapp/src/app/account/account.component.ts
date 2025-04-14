import { Component, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from "@angular/router";
import { ImageModule } from "primeng/image";
import { Divider } from "primeng/divider";
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Subject, takeUntil } from "rxjs";
import { Toast } from "primeng/toast";
import { ConfigService } from "../../shared/service/config.service";
import { ROOT_OBJECT } from "../../shared/const/app.const";
import { Action } from "../../shared/const/app.enum";
import { NgIf } from "@angular/common";
import { PermissionPipe } from "../../shared/pipe/permission.pipe";

@Component({
    selector: 'app-account',
    imports: [RouterOutlet, ImageModule, RouterLink, Divider, TranslatePipe, NgIf, PermissionPipe],
    templateUrl: './account.component.html',
    standalone: true,
    styleUrl: './account.component.scss'
})
export class AccountComponent implements OnDestroy {
    protected readonly ROOT_OBJECT = ROOT_OBJECT;
    protected readonly Action = Action;
    title: string = '';
    message: string = '';
    linkText: string = '';
    linkRoute: string = '';
    private destroy$ = new Subject<void>();

    constructor(
        private router: Router,
        private translate: TranslateService,
        private configService: ConfigService
    ) {
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
        } else if (url.includes('/account/send-email')) {
            this.translate.get(['CheckYourEmail', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['CheckYourEmail'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/account/register';
                });
        } else if (url.includes('/account/email-activation')) {
            this.translate.get(['VerifyEmail', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['VerifyEmail'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/account/register';
                });
        } else if (url.includes('/account/forgot-password')) {
            this.translate.get(['ForgotPasswordTitle', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['ForgotPasswordTitle'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/account/register';
                });
        } else if (url.includes('/account/reset-password')) {
            this.translate.get(['NewPassword', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['NewPassword'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/account/register';
                });
        } else if (url.includes('/account/send-code')) {
            this.translate.get(['SendCodeTitle', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['SendCodeTitle'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/account/register';
                });
        } else if (url.includes('/account/validate-code')) {
            this.translate.get(['VerifyCodeTitle', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['VerifyCodeTitle'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/account/register';
                });
        }


    }

    externalLogin(provider: string) {
        window.location.href = this.configService.oauthLoginUrl + provider;
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

}
