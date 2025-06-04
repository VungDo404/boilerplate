import { Component, OnDestroy, OnInit } from '@angular/core';
import { Avatar } from "primeng/avatar";
import { MenuComponent } from "../../menu/menu.component";
import { SessionService } from "../../../service/session.service";
import { LanguageService } from "../../../service/language.service";
import { Locale } from "../../../const/app.enum";
import { LangChangeEvent, TranslateService } from "@ngx-translate/core";
import { Subject, takeUntil } from "rxjs";

@Component({
    selector: 'app-header-avatar',
    imports: [
        Avatar,
        MenuComponent
    ],
    templateUrl: './header-avatar.component.html',
    standalone: true,
    styleUrl: './header-avatar.component.scss'
})
export class HeaderAvatarComponent implements OnInit, OnDestroy {
    protected items!: MenuData;
    private destroy$ = new Subject<void>();
    constructor(
        private sessionService: SessionService,
        private languageService: LanguageService,
        private translate: TranslateService
    ) {}

    private handleChangeLanguage(item: LinkItem){
        this.languageService.setLanguage(item.value)
    }

    ngOnInit(): void {
        this.prepareMenuItems();

        this.translate.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.prepareMenuItems();
            });

    }

    private prepareMenuItems(){
        this.loadMenuItems();
        this.addCheckIconForCurrentLanguage();
    }

    private addCheckIconForCurrentLanguage(){
        const currentLocale = this.languageService.getCurrentLanguage();
        for(const section of this.items.language.sections){
            for(const item of section.items){
                if(currentLocale === item.value){
                    item.leftIcon = "pi pi-check";
                    break;
                }
            }
        }
    }

    private loadMenuItems(): void {
        this.translate.get(['Profile', 'SignOut', 'Language', 'LanguageTitle'])
            .subscribe(translations => {
                this.items = {
                    main: {
                        sections: [
                            {
                                id: "profile",
                                items: [
                                    {
                                        type: "profile",
                                        displayName: this.sessionService.name,
                                        username: this.sessionService.username,
                                        avatar: this.sessionService.avatar
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
                                        leftIcon: "pi pi-user"
                                    },
                                    {
                                        type: "link",
                                        label: translations['SignOut'],
                                        routerLink: "/",
                                        leftIcon: "pi pi-sign-out",
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
                                    },
                                    {
                                        type: "link",
                                        label: 'Tiếng Việt',
                                        value: Locale.Vietnamese,
                                        cb: this.handleChangeLanguage.bind(this)
                                    }
                                ]
                            }
                        ]
                    }
                };
            });
    }

    get avatar(){
        return this.sessionService.avatar;
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }
}
