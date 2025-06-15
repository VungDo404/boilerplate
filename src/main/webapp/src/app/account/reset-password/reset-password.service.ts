import { Injectable } from '@angular/core';
import { Router } from "@angular/router";
import { NotifyService } from "../../../shared/service/notify.service";
import { TranslateService } from "@ngx-translate/core";
import { finalize, takeUntil } from "rxjs";
import { AccountService } from "../../../shared/service/http/account.service";
import { BaseComponent } from "../../../shared/component/base.component";

@Injectable({
    providedIn: 'root'
})
export class ResetPasswordService extends BaseComponent {

    constructor(
        private router: Router,
        private notifyService: NotifyService,
        private translate: TranslateService,
        private accountService: AccountService
    ) {super();}

    resetPassword(password: string, key: string, cb: () => void) {
        this.accountService.resetPassword(password, key).pipe(finalize(cb)).subscribe({
            next: (response) => {
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
                                this.router.navigate(['/login']);
                            }
                        );
                    });

            },
        })
    }

}
