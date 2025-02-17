import { Injectable } from '@angular/core';
import { ConfigService } from "../../../shared/service/config.service";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject } from "rxjs";
import { ToastMessageOptions } from "primeng/api";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class LoginService {
    private baseUrl!: string;
    private toastSource = new BehaviorSubject<ToastMessageOptions[]>([]);
    toastMessage$ = this.toastSource.asObservable();

    constructor(private configService: ConfigService, private http: HttpClient, private router: Router) {
        this.baseUrl = this.configService.baseUrl;
    }

    authenticate(loginForm: LoginForm, cb: () => void) {
        this.http.post<AuthenticationResult>(this.baseUrl + "auth/authenticate", loginForm).subscribe({
            next: (response) => {
                this.processAuthenticationResult(response);
                cb();
            },
            error: (error) => {
                cb();
                const problemDetail: ProblemDetail | ProblemDetailWithFieldError = error.error;
                if ('fieldErrors' in problemDetail) {
                    const messageOptions = problemDetail.fieldErrors.map(err => ({
                        severity: 'error',
                        summary: err.field,
                        detail: err.message
                    }))
                    this.toastSource.next(messageOptions);
                } else {
                    const messageOption = [{
                        severity: 'error',
                        summary: problemDetail.title,
                        detail: problemDetail.detail,
                    }]
                    this.toastSource.next(messageOption);
                }
            }
        });
    }

    private processAuthenticationResult(result: AuthenticationResult){
        if("requiresEmailVerification" in result){
            this.router.navigate(['/account/send-email'], {
                queryParams: { email:  result.email }
            });
        }
    }

}
