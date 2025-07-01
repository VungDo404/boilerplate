import { Component, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from "@angular/router";
import { ImageModule } from "primeng/image";
import { Divider } from "primeng/divider";
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { takeUntil } from "rxjs";
import { ConfigService } from "../../shared/service/config.service";
import { ROOT_OBJECT } from "../../shared/const/app.const";
import { Action } from "../../shared/const/app.enum";
import { NgIf } from "@angular/common";
import { PermissionPipe } from "../../shared/pipe/permission.pipe";
import { BaseComponent } from "../../shared/component/base.component";

@Component({
    selector: 'app-account',
    imports: [RouterOutlet, ImageModule, RouterLink, Divider, TranslatePipe, NgIf, PermissionPipe],
    templateUrl: './account.component.html',
    standalone: true,
    styleUrl: './account.component.scss'
})
export class AccountComponent extends BaseComponent{
    protected readonly ROOT_OBJECT = ROOT_OBJECT;
    protected readonly Action = Action;
    title: string = '';
    message: string = '';
    linkText: string = '';
    linkRoute: string = '';
    showExternalLogin = true;
    constructor(
        private router: Router,
        private translate: TranslateService,
        private configService: ConfigService
    ) {
        super();
        this.router.events
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.updateAuthPage();
            });
    }

    updateAuthPage() {
        const url = this.router.url;
        if (url.includes('/login')) {
            this.translate.get(['Authenticate', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['Authenticate'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/register';
                });
        } else if (url.includes('/register')) {
            this.translate.get(['CreateAccount', 'AlreadyHaveAccount', 'SignIn'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['CreateAccount'];
                    this.message = translations['AlreadyHaveAccount'];
                    this.linkText = translations['SignIn'];
                    this.linkRoute = '/login';
                });
        } else if (url.includes('/send-email')) {
            this.translate.get(['CheckYourEmail', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['CheckYourEmail'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/register';
                });
        } else if (url.includes('/email-activation')) {
            this.translate.get(['VerifyEmail', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['VerifyEmail'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/register';
                });
        } else if (url.includes('/forgot-password')) {
            this.translate.get(['ForgotPasswordTitle', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['ForgotPasswordTitle'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/register';
                });
        } else if (url.includes('/reset-password')) {
            this.translate.get(['NewPassword', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['NewPassword'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/register';
                });
        } else if (url.includes('/send-code')) {
            this.translate.get(['SendCodeTitle', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['SendCodeTitle'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/register';
                });
        } else if (url.includes('/validate-code')) {
            this.translate.get(['VerifyCodeTitle', 'DontHaveAccount', 'SignUp'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['VerifyCodeTitle'];
                    this.message = translations['DontHaveAccount'];
                    this.linkText = translations['SignUp'];
                    this.linkRoute = '/register';
                });
        }else{
            this.translate.get(['ReconfirmCredential'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['ReconfirmCredential'];
                });
            this.showExternalLogin = false;
        }
    }

    externalLogin(provider: string) {
        window.location.href = this.configService.oauthLoginUrl + provider;
    }

}
