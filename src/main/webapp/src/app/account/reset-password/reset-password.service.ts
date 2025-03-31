import { Injectable, OnDestroy } from '@angular/core';
import { Router } from "@angular/router";
import { NotifyService } from "../../../shared/service/notify.service";
import { TranslateService } from "@ngx-translate/core";
import { Subject, takeUntil } from "rxjs";
import Swal, { SweetAlertResult } from "sweetalert2";
import { AccountService } from "../../../shared/service/http/account.service";

@Injectable({
    providedIn: 'root'
})
export class ResetPasswordService implements OnDestroy {
    private destroy$ = new Subject<void>();

    constructor(
        private router: Router,
        private notifyService: NotifyService,
        private translate: TranslateService,
        private accountService: AccountService
    ) {}

    resetPassword(password: string, key: string, cb: () => void) {
        this.accountService.resetPassword(password, key).subscribe({
            next: (response) => {
                cb();

                this.translate.get(['ResetPasswordMessage', 'ResetPasswordSuccessfully', 'GoToLogin'])
                    .pipe(takeUntil(this.destroy$))
                    .subscribe(translations => {
                        const message = translations['ResetPasswordMessage'];
                        const title = translations['ResetPasswordSuccessfully'];

                        this.notifyService.success(
                            message,
                            title,
                            this.notifyService.option1(translations['GoToLogin']),
                            () => {
                                this.router.navigate(['/account/login']);
                            }
                        );
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
