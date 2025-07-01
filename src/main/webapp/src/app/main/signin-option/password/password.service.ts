import { Injectable } from '@angular/core';
import { AccountService } from "../../../../shared/service/http/account.service";
import { finalize } from "rxjs";
import { LogoutService } from "../../../account/logout/logout.service";
import { ToastService } from "../../../../shared/service/toast.service";
import { TranslateService } from "@ngx-translate/core";

@Injectable({
    providedIn: 'root'
})
export class PasswordService {

    constructor(
        private accountService: AccountService,
        private logoutService: LogoutService,
        private toastService: ToastService,
        private translate: TranslateService
    ) { }

    changePassword(body: ChangePassword, cb: () => void) {
        this.accountService.changePassword(body).pipe(finalize(cb)).subscribe({
            next: () => {
                this.translate.get(['Success', 'ChangePasswordMessage']).subscribe(translation => {
                    this.toastService.push('success', translation['Success'], translation['ChangePasswordMessage'], 'top-center');
                    setTimeout(() => {
                        const callBack = () => {
                            window.location.href = '/login';
                        }
                        this.logoutService.logout(callBack);
                    }, 1000)

                })

            }
        })
    }
}
