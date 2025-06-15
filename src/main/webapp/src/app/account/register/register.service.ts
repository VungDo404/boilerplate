import { Injectable } from '@angular/core';
import { Router } from "@angular/router";
import { AccountService } from "../../../shared/service/http/account.service";
import { finalize } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class RegisterService {

    constructor(private router: Router, private accountService: AccountService) {}

    register(registerForm: RegisterForm, cb: () => void) {
        this.accountService.register(registerForm).pipe(finalize(cb)).subscribe({
            next: (response) => {
                if (!response.canLogin) {
                    this.router.navigate(['/send-email'], {
                        queryParams: {email: registerForm.email}
                    });
                }

            },
        });
    }
}
