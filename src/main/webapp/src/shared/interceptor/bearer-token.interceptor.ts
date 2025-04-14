import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LocalStorageService } from "../service/local-storage.service";

@Injectable()
export class BearerTokenInterceptor implements HttpInterceptor {
    private readonly excludedUrls = [
        "/api/auth/refresh-token"
    ];
    constructor(private localStorageService: LocalStorageService) {}

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        const isSkip = this.excludedUrls.some(url => request.url.includes(url));
        if(!isSkip){
            const token = this.localStorageService.getAccessToken();
            if(token){
                request = request.clone({
                    setHeaders: {
                        Authorization: `Bearer ${token.accessToken}`
                    }
                });
            }
        }

        return next.handle(request);
    }
}
