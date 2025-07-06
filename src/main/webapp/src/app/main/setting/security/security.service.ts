import { Injectable } from '@angular/core';
import { BehaviorSubject } from "rxjs";
import { AccountService } from "../../../../shared/service/http/account.service";
import { SessionService } from "../../../../shared/service/session.service";

@Injectable({
    providedIn: 'root'
})
export class SecurityService {
    private securityInfoSubject = new BehaviorSubject<SecurityInfo | null>(null);
    securityInfo$ = this.securityInfoSubject.asObservable();
    constructor(private accountService: AccountService, private sessionService: SessionService) {
        this.getSecurityInfo();
    }

    private getSecurityInfo(){
        const id = this.sessionService.id;
        this.accountService.getSecurityInfo(id).subscribe({
            next: (response) => {
                this.securityInfoSubject.next({
                    twoFactorEnable: response.twoFactorEnable,
                    passwordLastUpdate: response.passwordLastUpdate
                })
            }
        })
    }
}
