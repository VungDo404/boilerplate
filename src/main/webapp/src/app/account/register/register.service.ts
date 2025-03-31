import { Injectable } from '@angular/core';
import { Router } from "@angular/router";
import { AuthenticationService } from "../../../shared/service/http/authentication.service";
import { AccountService } from "../../../shared/service/http/account.service";

@Injectable({
    providedIn: 'root'
})
export class RegisterService {

    constructor(private router: Router, private accountService: AccountService) {}

    register(registerForm: RegisterForm, cb: () => void) {
        this.accountService.register(registerForm).subscribe({
            next: (response) => {
                cb();
                if (!response.canLogin) {
                    this.router.navigate(['/account/send-email'], {
                        queryParams: {email: registerForm.email}
                    });
                }

            },
            error: cb
        });
    }
}
