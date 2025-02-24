import { Injectable } from '@angular/core';
import { ConfigService } from "../../../shared/service/config.service";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class SendTwoFactorCodeService {
    private baseUrl!: string;

    constructor(private configService: ConfigService, private http: HttpClient, private router: Router) {
        this.baseUrl = this.configService.baseUrl;
    }
    sendTwoFactorCode(model: SendTwoFactorCode,cb: () => void){
        this.http.post(this.baseUrl + "auth/send-code", model).subscribe({
            next: (response) => {
                cb();
                this.router.navigate(["account/validate-code"])
            },
            error: cb
        })
    }
}
