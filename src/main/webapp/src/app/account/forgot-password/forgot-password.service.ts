import { Injectable } from '@angular/core';
import { ConfigService } from "../../../shared/service/config.service";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class ForgotPasswordService {
    private baseUrl!: string;

    constructor(private configService: ConfigService, private http: HttpClient, private router: Router) {
        this.baseUrl = this.configService.baseUrl;
    }

    forgotPassword(email: string, cb: () => void) {
        this.http.post(this.baseUrl + "account/forgot-password", { email }).subscribe({
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
