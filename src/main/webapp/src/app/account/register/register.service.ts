import { Inject, Injectable } from '@angular/core';
import { ConfigService } from "../../../shared/service/config.service";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject } from "rxjs";
import { ToastMessageOptions } from "primeng/api";
import { Router } from "@angular/router";
import { APP_BASE_HREF } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {
  private baseUrl!: string;
  private toastSource = new BehaviorSubject<ToastMessageOptions[]>([]);
  toastMessage$ = this.toastSource.asObservable();
  constructor(private configService: ConfigService, private http: HttpClient, private router: Router) {
    this.baseUrl = this.configService.baseUrl;
  }
  register(registerForm: RegisterForm, cb: () => void){
    this.http.post<RegisterResult>(this.baseUrl + "account/register", registerForm).subscribe({
      next: (response) => {
        cb();
        if(!response.canLogin){
          this.router.navigate(['/account/send-email'], {
            queryParams: { email: registerForm.email }
          });
        }

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
}
