import { Injectable } from '@angular/core';
import { ConfigService } from "../../../shared/service/config.service";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { NotifyService } from "../../../shared/service/notify.service";
import { TranslateService } from "@ngx-translate/core";
import { Subject, takeUntil } from "rxjs";
import Swal, { SweetAlertResult } from "sweetalert2";

@Injectable({
    providedIn: 'root'
})
export class LoginService {
    private baseUrl!: string;
    private destroy$ = new Subject<void>();

    constructor(
        private configService: ConfigService,
        private http: HttpClient,
        private router: Router,
        private notifyService: NotifyService,
        private translate: TranslateService
    ) {
        this.baseUrl = this.configService.baseUrl;
    }

    authenticate(loginForm: LoginForm, cb: () => void) {
        this.http.post<AuthenticationResult>(this.baseUrl + "auth/authenticate", loginForm).subscribe({
            next: (response) => {
                this.processAuthenticationResult(response);
                cb();
            },
            error: cb
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
        }
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }
}
