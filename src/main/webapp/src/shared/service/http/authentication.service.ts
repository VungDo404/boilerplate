import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import { ConfigService } from "../config.service";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {
    private readonly baseUrl!: string;

    constructor(private http: HttpClient, private configService: ConfigService) {
        this.baseUrl = this.configService.baseUrl + "auth";
        
    }

    authenticate(body: LoginForm) {
        const url = this.baseUrl + "/authenticate";
        return this.http.post<AuthenticationResult>(url, body, { withCredentials: true });
    }

    sendTwoFactorCode(body: SendTwoFactorCode) {
        const url = this.baseUrl + "/send-code";
        return this.http.post(url, body);
    }

    refreshToken(){
        const url = this.baseUrl + "/refresh-token";
        return this.http.post<AuthenticationTokenResult>(url, {}, { withCredentials: true });
    }

    logout(){
        const url = this.baseUrl + "/logout";
        return this.http.post(url, {}, { withCredentials: true });
    }

    isConfirmSecurity(id: string){
        const url = this.baseUrl + "/confirm-credential/" + id;
        return this.http.get<IsConfirmSecurity>(url);
    }

    confirmSecurity(body: ConfirmSecurity, id: string){
        const url = this.baseUrl + "/confirm-credential/" + id;
        return this.http.post(url, body, { withCredentials: true });
    }
}
