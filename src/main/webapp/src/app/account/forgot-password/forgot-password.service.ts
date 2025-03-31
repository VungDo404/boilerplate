import { Injectable } from '@angular/core';
import { Router } from "@angular/router";
import { AuthenticationService } from "../../../shared/service/http/authentication.service";
import { AccountService } from "../../../shared/service/http/account.service";

@Injectable({
    providedIn: 'root'
})
export class ForgotPasswordService {

    constructor(private router: Router, private accountService: AccountService) {}

    forgotPassword(email: string, cb: () => void) {
        this.accountService.forgotPassword(email).subscribe({
            next: (response) => {
                cb();
                this.router.navigate(['/account/send-email'], {
                    queryParams: { email }
                });
            },
            error: cb
        })
    }
}
