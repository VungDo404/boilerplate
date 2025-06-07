import { Injectable } from '@angular/core';
import { LanguageService } from "../../service/language.service";
import { TranslateService } from "@ngx-translate/core";
import { SessionService } from "../../service/session.service";
import { BehaviorSubject, combineLatest, map, Observable, Subject, takeUntil } from "rxjs";
import { Action, Locale } from "../../const/app.enum";
import { ROOT_OBJECT } from "../../const/app.const";

@Injectable({
    providedIn: 'root'
})
export class HeaderMenuService {
    item$!: Observable<MenuData>;
    private destroy$ = new Subject<void>();
    private readonly EMPTY_MENU_DATA: MenuData = {
        main: { sections: [] },
        language: { title: '', sections: [] }
    };
    protected itemSubject = new BehaviorSubject<MenuData>(this.EMPTY_MENU_DATA);

    constructor(private languageService: LanguageService, private translate: TranslateService, private sessionService: SessionService) {
        this.item$ = this.itemSubject.asObservable();
    }

    get getItemSubject(){
        return this.itemSubject;
    }

    private handleChangeLanguage(item: LinkItem) {
        this.languageService.setLanguage(item.value)
    }

    private addCheckIconForCurrentLanguage(items: MenuData) {
        const currentLocale = this.languageService.getCurrentLanguage();
        for (const section of items.language.sections) {
            for (const item of section.items) {
                if (currentLocale === item.value) {
                    item.leftIcon = "pi pi-check";
                    break;
                }
            }
        }
        return items;
    }

     loadMenuItems(): void {
        combineLatest(
            [this.translate.get(['Profile', 'SignOut', 'Language', 'LanguageTitle']), this.sessionService.sessionState$])
            .pipe(
                takeUntil(this.destroy$),
                map(([translations, sessionState]) => {
                        const res: MenuData = {
                            main: {
                                sections: [
                                    {
                                        id: "profile",
                                        items: [
                                            {
                                                type: "profile",
                                                displayName: sessionState.name,
                                                username: sessionState.username,
                                                avatar: sessionState.avatar,
                                                authority: { id: sessionState.userId, type: "User", mask: Action.Write }
                                            },
                                        ]
                                    },
                                    {
                                        id: "account",
                                        items: [
                                            {
                                                type: "link",
                                                label: translations['Profile'],
                                                routerLink: "/",
                                                leftIcon: "pi pi-user",
                                                authority: { id: sessionState.userId, type: "User", mask: Action.Write }
                                            },
                                            {
                                                type: "link",
                                                label: translations['SignOut'],
                                                routerLink: "/account/logout",
                                                leftIcon: "pi pi-sign-out",
                                                authority: { id: sessionState.userId, type: "User", mask: Action.Write }
                                            }
                                        ]
                                    },
                                    {
                                        id: "setting",
                                        items: [
                                            {
                                                type: "link",
                                                label: translations['Language'],
                                                target: "language",
                                                leftIcon: "pi pi-globe",
                                                rightIcon: "pi pi-chevron-right",
                                                authority: { id: ROOT_OBJECT, type: "Authentication", mask: Action.Delete }
                                            }
                                        ]
                                    }
                                ]
                            },
                            language: {
                                title: translations['LanguageTitle'],
                                sections: [
                                    {
                                        id: 'option',
                                        items: [
                                            {
                                                type: "link",
                                                label: 'English (UK)',
                                                value: Locale.English,
                                                cb: this.handleChangeLanguage.bind(this),
                                                authority: { id: ROOT_OBJECT, type: "Authentication", mask: Action.Delete }
                                            },
                                            {
                                                type: "link",
                                                label: 'Tiếng Việt',
                                                value: Locale.Vietnamese,
                                                cb: this.handleChangeLanguage.bind(this),
                                                authority: { id: ROOT_OBJECT, type: "Authentication", mask: Action.Delete }
                                            }
                                        ]
                                    }
                                ]
                            }
                        };
                        return res;
                    }
                ),
                map(menu => this.addCheckIconForCurrentLanguage(menu))
            )
            .subscribe((items) => this.itemSubject.next(items))
    }

    dispose() {
        this.destroy$.next();
        this.destroy$.complete();
    }
}
