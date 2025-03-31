import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { BehaviorSubject, catchError, finalize, Observable, switchMap, take, throwError, filter } from 'rxjs';
import { ToastService } from "../service/toast.service";
import { AuthenticationService } from "../service/http/authentication.service";
import { Router } from "@angular/router";
import { NotifyService } from "../service/notify.service";
import { TranslateService } from "@ngx-translate/core";
import { LoginService } from "../../app/account/login/login.service";

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
    private readonly INVALID_TOKEN = "invalid_token";
    private isRefreshingToken = false;
    private tokenSubject = new BehaviorSubject<string | null>(null);
    constructor(
        private toastService: ToastService,
        private authenticationService: AuthenticationService,
        private router: Router,
        private notifyService: NotifyService,
        private translateService: TranslateService,
        private loginService: LoginService
    ) {}

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        return next.handle(request)
            .pipe(
                catchError((error: HttpErrorResponse) => {
                    if (error.error) {
                        const problemDetail: ProblemDetail | ProblemDetailWithFieldError = error.error;
                        if (error.status === 401 && problemDetail.instance === "/api/auth/refresh-token") {
                            return throwError(() => error);
                        }
                        const wwwAuthenticate = error.headers.get('WWW-Authenticate');
                        if (error.status === 401 && this.is401InvalidToken(problemDetail, wwwAuthenticate)) {
                            return this.process401InvalidToken(request, next);
                        }

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

    private process401InvalidToken(request: HttpRequest<unknown>, next: HttpHandler) {
        if(!this.isRefreshingToken){
            this.isRefreshingToken = true;
            this.tokenSubject.next(null);
            return this.authenticationService.refreshToken().pipe(
                switchMap((response) => {
                    this.loginService.login(response, () => {});
                    this.tokenSubject.next(response.accessToken);
                    return next.handle(this.updateRequestWithNewToken(request, response.accessToken));
                }),
                catchError((err) => {
                    this.translateService.get(['SessionExpired', 'SessionExpiredMessage', 'SignIn']).subscribe(
                        translations => {
                            this.notifyService.error(
                                translations['SessionExpiredMessage'],
                                translations['SessionExpired'],
                                this.notifyService.option1(translations['SignIn']),
                                () => {
                                    this.router.navigate(['/account/login']);
                                }
                            )
                        }
                    );
                    this.tokenSubject.error(err);
                    return throwError(() => err);
                }),
                finalize(() => {
                    this.isRefreshingToken = false;
                })
            );
        }else{
            return this.tokenSubject.pipe(
                filter(token => token !== null),
                take(1),
                switchMap(token => {
                    return next.handle(this.updateRequestWithNewToken(request, token!));
                })
            );
        }

    }

    private updateRequestWithNewToken(request: HttpRequest<unknown>, newToken: string) {
        return request.clone({
            setHeaders: {
                Authorization: `Bearer ${newToken}`
            }
        });
    }

    private is401InvalidToken(problemDetail: ProblemDetail, wwwAuthenticate: string | null) {
        const p1 = problemDetail.detail === this.INVALID_TOKEN;
        const p2 = !!(wwwAuthenticate && this.iswwwAuthenticateInvalidToken(wwwAuthenticate));
        return (p1 && p2);

    }

    private iswwwAuthenticateInvalidToken(wwwAuthenticate: string) {
        const match = wwwAuthenticate.match(/error="([^"]+)"/);
        return match?.[1] === this.INVALID_TOKEN;
    }
}
