import { Injectable } from '@angular/core';
import { ConfigService } from "../../../shared/service/config.service";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class RegisterService {
    private baseUrl!: string;

    constructor(private configService: ConfigService, private http: HttpClient, private router: Router) {
        this.baseUrl = this.configService.baseUrl;
    }

    register(registerForm: RegisterForm, cb: () => void) {
        this.http.post<RegisterResult>(this.baseUrl + "account/register", registerForm).subscribe({
            next: (response) => {
                cb();
                if (!response.canLogin) {
                    this.router.navigate(['/account/send-email'], {
                        queryParams: {email: registerForm.email}
                    });
                }

            },
            error: (error) => {
                cb();
            }
        });
    }
}
