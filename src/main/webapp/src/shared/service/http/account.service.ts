import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import { ConfigService } from "../config.service";

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    private readonly baseUrl!: string;

    constructor(private http: HttpClient, private configService: ConfigService) {
        this.baseUrl = this.configService.baseUrl + "account";
    }

    emailActivation(key: string) {
        const params = new HttpParams().set('key', key);
        const url = this.baseUrl + "/email-activation";
        return this.http.get(url, { params });
    }

    forgotPassword(email: string) {
        const url = this.baseUrl + "/forgot-password";
        return this.http.post(url, { email })
    }

    register(registerForm: RegisterForm) {
        const url = this.baseUrl + "/register";
        return this.http.post<RegisterResult>(url, registerForm)
    }

    resetPassword(password: string, key: string) {
        const url = this.baseUrl + "/reset-password";
        const params = new HttpParams().set('key', key);
        return this.http.post(url, { password }, { params })
    }

    profile(){
        const url = this.baseUrl + "/profile";
        return this.http.get<ProfileResult>(url, {})
    }

    avatar(id: string, file: File){
        const url = this.baseUrl + "/avatar/user/" + id;
        const formData = new FormData();
        formData.append('file', file);
        return this.http.patch<string>(url, formData);
    }

    updateUserInfo(id: string, body: UpdateUserInfo){
        const url = this.baseUrl + "/user/" + id;
        return this.http.patch(url, body);
    }

    getUpdateUserInfo(id: string){
        const url = this.baseUrl + "/user/" + id;
        return this.http.get<UpdateUserInfo>(url);
    }

    getSecurityInfo(id: string){
        const url = this.baseUrl + "/user/" + id + "/security";
        return this.http.get<SecurityInfo>(url);
    }

    changePassword(body: ChangePassword){
        const url = this.baseUrl + "/change-password";
        return this.http.post(url, body);
    }
}
