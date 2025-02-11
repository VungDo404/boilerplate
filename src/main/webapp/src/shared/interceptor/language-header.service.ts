import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { of, Observable, switchMap } from "rxjs";
import { LanguageService } from "../service/language.service";

@Injectable({
    providedIn: 'root'
})
export class LanguageHeaderService implements HttpInterceptor {
    constructor(private languageService: LanguageService) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return of(this.languageService.getCurrentLanguage()).pipe(
            switchMap(language => {
                const lang = language || this.languageService.DEFAULT_LANGUAGE;
                const modifiedRequest = request.clone({
                    setHeaders: {
                        'Accept-Language': lang
                    }
                });
                return next.handle(modifiedRequest);
            })
        );
    }
}
