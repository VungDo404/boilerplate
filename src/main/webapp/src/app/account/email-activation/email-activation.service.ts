import { Injectable } from '@angular/core';
import { AccountService } from "../../../shared/service/http/account.service";

@Injectable({
    providedIn: 'root'
})
export class EmailActivationService {

    constructor(private accountService: AccountService) {}

    verify(key: string, cbSuccess: () => void, cbError: () => void) {
        this.accountService.emailActivation(key).subscribe({
                next: cbSuccess,
                error: cbError
            }
        );
    }
}
