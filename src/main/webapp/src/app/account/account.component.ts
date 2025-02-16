import { Component, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from "@angular/router";
import { ImageModule } from "primeng/image";
import { Divider } from "primeng/divider";
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { merge, Subject, Subscription, takeUntil } from "rxjs";
import { Toast } from "primeng/toast";
import { MessageService, ToastMessageOptions } from "primeng/api";
import { LoginService } from "./login/login.service";
import { RegisterService } from "./register/register.service";

@Component({
    selector: 'app-account',
    imports: [RouterOutlet, ImageModule, RouterLink, Divider, TranslatePipe, Toast],
    providers: [MessageService],
    templateUrl: './account.component.html',
    standalone: true,
    styleUrl: './account.component.scss'
})
export class AccountComponent implements OnDestroy {
    title: string = '';
    message: string = '';
    linkText: string = '';
    linkRoute: string = '';
    toastMessageOptions: ToastMessageOptions[] = [];
    private toastSubscription!: Subscription;
    private destroy$ = new Subject<void>();

    constructor(
        private router: Router,
        private translate: TranslateService,
        private loginService: LoginService,
        private registerService: RegisterService,
        private messageService: MessageService
    ) {
        this.router.events
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.updateAuthPage();
            });
        this.toastSubscription = merge(
            this.loginService.toastMessage$,
            this.registerService.toastMessage$
        ).subscribe((option: ToastMessageOptions[]) => {
            this.toastMessageOptions = option;
            this.messageService.addAll(this.toastMessageOptions);
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
            this.translate.get(['CheckYourEmail'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['CheckYourEmail'];
                });
        } else if (url.includes('/account/email-activation')) {
            this.translate.get(['VerifyEmail'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['VerifyEmail'];
                });
        } else if (url.includes('/account/forgot-password')) {
            this.translate.get(['ForgotPasswordTitle'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.title = translations['ForgotPasswordTitle'];
                });
        }
    }


    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

}
