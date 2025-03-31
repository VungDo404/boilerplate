import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import { ConfigService } from "../config.service";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {
    private readonly baseUrl!: string;

    constructor(private http: HttpClient, private configService: ConfigService) {
        this.baseUrl = this.configService.baseUrl + "auth/";
        
    }

    authenticate(body: LoginForm) {
        const url = this.baseUrl + "authenticate";
        return this.http.post<AuthenticationResult>(url, body);
    }

    sendTwoFactorCode(body: SendTwoFactorCode) {
        const url = this.baseUrl + "send-code";
        return this.http.post(url, body);
    }

    refreshToken(){
        const url = this.baseUrl + "refresh-token";
        return this.http.post<AuthenticationTokenResult>(url, {});
    }
}
