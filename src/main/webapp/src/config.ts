import {
    ApplicationConfig,
    importProvidersFrom,
    inject,
    provideAppInitializer,
    provideZoneChangeDetection
} from '@angular/core';

import { provideAnimationsAsync } from "@angular/platform-browser/animations/async";
import { providePrimeNG } from "primeng/config";
import Aura from '@primeng/themes/aura';
import { provideRouter } from "@angular/router";
import { ROUTE } from "./root.route";
import { HTTP_INTERCEPTORS, HttpClient, provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { TranslateLoader, TranslateModule } from "@ngx-translate/core";
import { TranslateHttpLoader } from "@ngx-translate/http-loader";
import { LanguageHeaderInterceptor } from "./shared/interceptor/language-header.interceptor";
import { LanguageService } from "./shared/service/language.service";
import { HttpErrorInterceptor } from "./shared/interceptor/http-error.interceptor";
import { BearerTokenInterceptor } from "./shared/interceptor/bearer-token.interceptor";
import { SessionService } from "./shared/service/session.service";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";

export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

export const config: ApplicationConfig = {
    providers: [
        provideRouter(ROUTE),
        provideZoneChangeDetection({ eventCoalescing: true }),
        provideAnimationsAsync(),
        providePrimeNG({
            theme: {
                preset: Aura,
                options: {
                    darkModeSelector: '.my-app-dark'
                }
            }
        }),
        provideHttpClient(withInterceptorsFromDi()),
        importProvidersFrom(
            TranslateModule.forRoot({
                loader: {
                    provide: TranslateLoader,
                    useFactory: HttpLoaderFactory,
                    deps: [HttpClient]
                }
            }), BrowserAnimationsModule),
        {
            provide: HTTP_INTERCEPTORS,
            useClass: LanguageHeaderInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: BearerTokenInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: HttpErrorInterceptor,
            multi: true
        },
        provideAppInitializer(() => {
            const languageService = inject(LanguageService);
            return languageService.initializeLanguage();
        }),
        provideAppInitializer(() => {
            const sessionService = inject(SessionService);
            return sessionService.profile();
        })
    ]
};
