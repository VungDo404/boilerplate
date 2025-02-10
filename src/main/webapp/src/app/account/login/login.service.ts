import { Injectable } from '@angular/core';
import { LoginForm } from "./login.component";
import { ConfigService } from "../../../shared/service/config.service";
import { HttpClient } from "@angular/common/http";
import * as localForage from 'localforage';

@Injectable({
    providedIn: 'root'
})
export class LoginService {
    private baseUrl!: string;

    constructor(private configService: ConfigService, private http: HttpClient) {
        this.baseUrl = this.configService.baseUrl;
    }

    authenticate(loginForm: LoginForm, cb: () => void) {
        this.http.post<AuthenticationResult>(this.baseUrl + "auth/authenticate", loginForm).subscribe({
            next: (response ) => {

            },
            error: (error) => {
                console.error("Authentication failed:", error);
            }
        });
        cb();
    }

}
