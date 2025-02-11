import { Injectable } from '@angular/core';
import { DEFAULT_LANGUAGE, TranslateService } from "@ngx-translate/core";
import * as localforage from 'localforage';

@Injectable({
    providedIn: 'root'
})
export class LanguageService {
    public readonly STORAGE_KEY = 'app_language';
    public readonly DEFAULT_LANGUAGE = 'vi';
    private readonly SUPPORTED_LANGUAGES = ['en', 'vi'];

    constructor(private translate: TranslateService) {}

    initializeLanguage() {
        this.translate.setDefaultLang(this.DEFAULT_LANGUAGE);
        this.translate.addLangs(this.SUPPORTED_LANGUAGES);
        this.setLanguage(this.DEFAULT_LANGUAGE);

        const storedLanguage = localStorage.getItem(this.STORAGE_KEY);
        if (storedLanguage && this.translate.getLangs().includes(storedLanguage)) {
            this.translate.use(storedLanguage);
        } else {
            this.translate.use(this.DEFAULT_LANGUAGE);
            localStorage.setItem(this.STORAGE_KEY, this.DEFAULT_LANGUAGE);
        }
    }

    setLanguage(language: string) {
        if (!this.SUPPORTED_LANGUAGES.includes(language)) {
            language = this.DEFAULT_LANGUAGE;
        }
        localStorage.setItem(this.STORAGE_KEY, language);
        this.translate.use(language);
        return language;
    }

    getCurrentLanguage(): string {
        const l = localStorage.getItem(this.STORAGE_KEY);
        return l ?? this.DEFAULT_LANGUAGE;

    }
}
