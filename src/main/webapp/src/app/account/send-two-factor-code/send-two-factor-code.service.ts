import { Injectable } from '@angular/core';
import { Router } from "@angular/router";
import { AuthenticationService } from "../../../shared/service/http/authentication.service";
import { finalize } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class SendTwoFactorCodeService {
    constructor(private router: Router, private authenticationService: AuthenticationService) {}
    sendTwoFactorCode(model: SendTwoFactorCode,cb: () => void){
        this.authenticationService.sendTwoFactorCode(model).pipe(finalize(cb)).subscribe({
            next: (response) => {
                this.router.navigate(["account/validate-code"])
            }
        })
    }
}
