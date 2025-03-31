import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import { ConfigService } from "../config.service";

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    private readonly baseUrl!: string;

    constructor(private http: HttpClient, private configService: ConfigService) {
        this.baseUrl = this.configService.baseUrl + "account/";
    }

    emailActivation(key: string) {
        const params = new HttpParams().set('key', key);
        const url = this.baseUrl + "email-activation";
        return this.http.get(url, { params });
    }

    forgotPassword(email: string) {
        const url = this.baseUrl + "forgot-password";
        return this.http.post(url, { email })
    }

    register(registerForm: RegisterForm) {
        const url = this.baseUrl + "register";
        return this.http.post<RegisterResult>(url, registerForm)
    }

    resetPassword(password: string, key: string) {
        const url = this.baseUrl + "reset-password";
        const params = new HttpParams().set('key', key);
        return this.http.post(url, { password }, { params })
    }
}
