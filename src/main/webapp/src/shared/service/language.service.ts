import { Injectable } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";
import { Locale } from "../const/app.enum";

@Injectable({
    providedIn: 'root'
})
export class LanguageService {
    public readonly STORAGE_KEY = 'app_language';
    public readonly DEFAULT_LANGUAGE = Locale.English;
    private readonly SUPPORTED_LANGUAGES: Locale[] = [Locale.English, Locale.Vietnamese];

    constructor(private translate: TranslateService) {}

    initializeLanguage() {
        this.translate.setDefaultLang(this.DEFAULT_LANGUAGE);
        this.translate.addLangs(this.SUPPORTED_LANGUAGES);

        const storedLanguage = localStorage.getItem(this.STORAGE_KEY);
        if (storedLanguage && this.translate.getLangs().includes(storedLanguage)) {
            this.translate.use(storedLanguage);
        } else {
            this.setLanguage(this.DEFAULT_LANGUAGE);
            this.translate.use(this.DEFAULT_LANGUAGE);
            localStorage.setItem(this.STORAGE_KEY, this.DEFAULT_LANGUAGE);
        }
    }

    setLanguage(language?: string | Locale) {
        const resolvedLanguage = typeof language === 'string'
            ? this.toLocale(language)
            : language;

        const finalLanguage = this.SUPPORTED_LANGUAGES.includes(resolvedLanguage as Locale)
            ? resolvedLanguage as Locale
            : this.DEFAULT_LANGUAGE;

        localStorage.setItem(this.STORAGE_KEY, finalLanguage);
        this.translate.use(finalLanguage);
        return finalLanguage;
    }

    getCurrentLanguage(): string {
        const l = localStorage.getItem(this.STORAGE_KEY);
        return l ?? this.DEFAULT_LANGUAGE;

    }

    toLocale(l: string){
        return Object.values(Locale).includes(l as Locale)
            ? (l as Locale)
            : undefined;
    }
}
