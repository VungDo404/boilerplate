import { Injectable } from '@angular/core';
import { ConfigService } from "../../../shared/service/config.service";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { NotifyService } from "../../../shared/service/notify.service";
import { TranslateService } from "@ngx-translate/core";
import { Subject, takeUntil } from "rxjs";
import Swal, { SweetAlertResult } from "sweetalert2";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";

@Injectable({
    providedIn: 'root'
})
export class LoginService {
    private baseUrl!: string;
    private destroy$ = new Subject<void>();
    loginForm!: FormGroup;

    constructor(
        private formBuilder: FormBuilder,
        private configService: ConfigService,
        private http: HttpClient,
        private router: Router,
        private notifyService: NotifyService,
        private translate: TranslateService
    ) {
        this.baseUrl = this.configService.baseUrl;
    }

    authenticate(cb: () => void) {
        this.http.post<AuthenticationResult>(this.baseUrl + "auth/authenticate", this.loginForm.value).subscribe({
            next: (response) => {
                this.processAuthenticationResult(response);
                cb();
            },
            error: cb
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
                ''
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
                    const option = {
                        icon: 'info',
                        showConfirmButton: true,
                        confirmButtonText: translations['OK'],
                        allowOutsideClick: false,
                        confirmButtonColor: '#EA6365',
                        timer: 4000,
                        timerProgressBar: true,
                    }
                    const cb = (alertResult: SweetAlertResult<any>) => {
                        if (alertResult.isConfirmed || alertResult.dismiss === Swal.DismissReason.timer)
                            this.router.navigate(['/account/reset-password'], {
                                queryParams: { key: result.passwordResetCode }
                            });
                    }
                    this.notifyService.info(message, title, option, cb);
                });
        } else if ("isTwoFactorEnabled" in result && result.isTwoFactorEnabled) {
            this.router.navigate(['/account/send-code'], {
                state: { loginResult: result }
            });
        }else if("accessToken" in result){
            this.login(result)
        }else {
            this.router.navigate(['/account/login'])
        }
    }

    private login(result: AuthenticationTokenResult){
        this.router.navigate(["/main"])
    }
}
