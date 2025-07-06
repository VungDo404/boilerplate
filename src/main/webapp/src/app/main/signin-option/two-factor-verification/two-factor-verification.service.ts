import { Injectable } from '@angular/core';
import { AccountService } from "../../../../shared/service/http/account.service";
import { SessionService } from "../../../../shared/service/session.service";
import { finalize } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class TwoFactorVerificationService {

    constructor(private accountService: AccountService, private sessionService: SessionService) { }

    twoFactorInfo(){
        return this.accountService.getTwoFactor(this.sessionService.id);
    }

    enable2FA(cb: () => void){
        this.accountService.enableTwoFactor(this.sessionService.id).subscribe({
            next: cb
        })
    }

    disable(cb: () => void){
        this.accountService.disableTwoFactor(this.sessionService.id).subscribe({
            next: cb
        })
    }

    enableAuthenticator(body: EnableAuthenticator, cb: () => void){
        this.accountService.enableAuthenticator(this.sessionService.id, body).subscribe({
            next: cb
        })
    }

    disableAuthenticator(cb: () => void){
        this.accountService.disableAuthenticator(this.sessionService.id).subscribe({
            next: cb
        })
    }

    getAuthenticatorInfo(){
        return this.accountService.getAuthenticator(this.sessionService.id);
    }
}
