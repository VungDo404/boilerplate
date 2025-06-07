import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from "@angular/router";
import { NotifyService } from "../../../shared/service/notify.service";
import { TranslateService } from "@ngx-translate/core";
import { finalize, Subject, takeUntil } from "rxjs";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { LocalStorageService } from "../../../shared/service/local-storage.service";
import { AuthenticationService } from "../../../shared/service/http/authentication.service";

@Injectable({
    providedIn: 'root'
})
export class LoginService {
    private destroy$ = new Subject<void>();
    loginForm!: FormGroup;
    redirectUrl: string | undefined;

    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private notifyService: NotifyService,
        private translate: TranslateService,
        private localStorageService: LocalStorageService,
        private authenticationService: AuthenticationService
    ) {}

    authenticate(cb: () => void) {
        this.authenticationService.authenticate(this.loginForm.value).pipe(finalize(cb)).subscribe({
            next: (response) => {
                this.processAuthenticationResult(response);
            },
        });
    }

    initForm() {
        this.loginForm = this.formBuilder.group({
            username: [
                '',
                [
                    Validators.required,
                    Validators.minLength(3),
                    Validators.maxLength(50),
                    Validators.pattern(/^[\w!@#$%^&*()\-=+<>?,.;:'"{}\[\]\\\/|`~]+$/)
                ]
            ],
            password: [
                '',
                [
                    Validators.required,
                    Validators.minLength(6),
                    Validators.maxLength(60),
                    Validators.pattern(/^[\w!@#$%^&*()\-=+<>?,.;:'"{}\[\]\\\/|`~]+$/)
                ]
            ],
            twoFactorCode: [
                '',
                [
                    Validators.maxLength(6)
                ]
            ]
        });
    }

    private processAuthenticationResult(result: AuthenticationResult) {
        if ("requiresEmailVerification" in result) {
            this.router.navigate(['/account/send-email'], {
                queryParams: { email: result.email }
            });
        } else if ("shouldChangePasswordOnNextLogin" in result && result.shouldChangePasswordOnNextLogin) {
            this.translate.get(['SecurityPolicy', 'ShouldChangePasswordMessage', 'OK'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    const title = translations['SecurityPolicy'];
                    const message = translations['ShouldChangePasswordMessage'];

                    this.notifyService.info(
                        message,
                        title,
                        this.notifyService.option1(translations['OK']),
                        () => {
                            this.router.navigate(['/account/reset-password'], {
                                queryParams: { key: result.passwordResetCode }
                            });
                        }
                    );
                });
        } else if ("isTwoFactorEnabled" in result && result.isTwoFactorEnabled) {
            this.router.navigate(['/account/send-code'], {
                state: { loginResult: result }
            });
        } else if ("accessToken" in result) {
            this.login(result, () => {
                window.location.href = this.redirectUrl ?? '/main';
                this.redirectUrl = undefined;
            })
        } else {
            this.router.navigate(['/account/login'])
        }
    }

    login(result: AuthenticationTokenResult, cb: () => void) {
        const tokenExpireDate = new Date(new Date().getTime() + 1000 * result.expiresInSeconds);
        this.localStorageService.setAccessToken(result.accessToken, tokenExpireDate);
        cb();
    }
}
