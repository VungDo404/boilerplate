import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { ToastService } from "../service/toast.service";

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

  constructor(private toastService: ToastService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request)
        .pipe(
            catchError((error: HttpErrorResponse) => {
              if (error.error) {
                const problemDetail: ProblemDetail | ProblemDetailWithFieldError = error.error;
                const toastSource = this.toastService.toastSource;

                if ('fieldErrors' in problemDetail) {
                  const messageOptions = problemDetail.fieldErrors.map(err => ({
                    severity: 'error',
                    summary: err.field,
                    detail: err.message
                  }))
                  toastSource.next(messageOptions);
                } else {
                  const messageOption = [{
                    severity: 'error',
                    summary: problemDetail.title,
                    detail: problemDetail.detail,
                  }]
                  toastSource.next(messageOption);
                }
              }
              return throwError(() => error);
            })
        );
  }
}
