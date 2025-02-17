import { Injectable, OnDestroy } from '@angular/core';
import { ConfigService } from "../../../shared/service/config.service";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Router } from "@angular/router";
import { NotifyService } from "../../../shared/service/notify.service";
import { TranslateService } from "@ngx-translate/core";
import { Subject, takeUntil } from "rxjs";
import Swal, { SweetAlertResult } from "sweetalert2";

@Injectable({
    providedIn: 'root'
})
export class ResetPasswordService implements OnDestroy {
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

    resetPassword(password: string, key: string, cb: () => void) {
        const params = new HttpParams().set('key', key);
        this.http.post(this.baseUrl + "account/reset-password", { password }, { params }).subscribe({
            next: (response) => {
                cb();

                this.translate.get(['ResetPasswordMessage', 'ResetPasswordSuccessfully', 'GoToLogin'])
                    .pipe(takeUntil(this.destroy$))
                    .subscribe(translations => {
                        const message = translations['ResetPasswordMessage'];
                        const title = translations['ResetPasswordSuccessfully'];
                        const option = {
                            icon: 'success',
                            showConfirmButton: true,
                            confirmButtonText: translations['GoToLogin'],
                            allowOutsideClick: false,
                            confirmButtonColor: '#EA6365',
                            timer: 4000,
                            timerProgressBar: true,
                        }
                        const cb = (alertResult: SweetAlertResult<any>) => {
                            if (alertResult.isConfirmed || alertResult.dismiss === Swal.DismissReason.timer)
                                this.router.navigate(['/account/login']);
                        }
                        this.notifyService.success(message, title, option, cb);
                    });

            },
            error: cb
        })
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }
}
