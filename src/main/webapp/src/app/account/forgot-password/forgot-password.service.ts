import { Injectable } from '@angular/core';
import { Router } from "@angular/router";
import { AccountService } from "../../../shared/service/http/account.service";
import { finalize } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class ForgotPasswordService {

    constructor(private router: Router, private accountService: AccountService) {}

    forgotPassword(email: string, cb: () => void) {
        this.accountService.forgotPassword(email).pipe(finalize(cb)).subscribe({
            next: (response) => {
                this.router.navigate(['/send-email'], {
                    queryParams: { email }
                });
            },
        })
    }
}
