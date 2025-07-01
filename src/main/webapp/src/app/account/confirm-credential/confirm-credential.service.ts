import { Injectable } from '@angular/core';
import { AuthenticationService } from "../../../shared/service/http/authentication.service";
import { BehaviorSubject, finalize, map } from "rxjs";
import { SessionService } from "../../../shared/service/session.service";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class ConfirmCredentialService {

    constructor(private authenticationService: AuthenticationService, private sessionService: SessionService, private router: Router) {
        this.isConfirmCredential();
    }

    confirmCredential(body: ConfirmSecurity, cb: () => void, redirect: string){
        this.authenticationService.confirmSecurity(body, this.sessionService.id).pipe(finalize(cb)).subscribe({
            next: () => {
                this.router.navigate([redirect]);
            }
        })
    }

    isConfirmCredential(){
        return this.authenticationService.isConfirmSecurity(this.sessionService.id).pipe(
            map(res => res.confirmed)
        );
    }
}
