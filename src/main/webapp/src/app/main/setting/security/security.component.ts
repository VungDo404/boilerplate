import { Component, OnInit } from '@angular/core';
import { LangChangeEvent, TranslatePipe, TranslateService } from "@ngx-translate/core";
import { AccordionComponent, AccordionItem } from "../../../../shared/component/accordion/accordion.component";
import { SecurityService } from "./security.service";
import { BaseComponent } from "../../../../shared/component/base.component";
import { filter, map, switchMap, takeUntil } from "rxjs";
import { BusyDirective } from "../../../../shared/directive/busy.directive";
import { LanguageService } from "../../../../shared/service/language.service";
import { AccordionItemComponent } from "../../../../shared/component/accordion/accordion-item/accordion-item.component";
import { NgForOf } from "@angular/common";

@Component({
    selector: 'app-security',
    imports: [
        TranslatePipe,
        AccordionComponent,
        BusyDirective,
        AccordionItemComponent,
        NgForOf
    ],
    templateUrl: './security.component.html',
    standalone: true,
    styleUrl: './security.component.scss'
})
export class SecurityComponent extends BaseComponent implements OnInit {
    securityItems!: AccordionItem[];
    loading = true;

    constructor(
        private securityService: SecurityService,
        private translate: TranslateService,
        private languageService: LanguageService
    ) {super();}

    ngOnInit(): void {
        this.loadSecurityItems();
        this.translate.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.loadSecurityItems();
            });
    }

    private loadSecurityItems() {

        this.securityService.securityInfo$.pipe(
            filter(data => data !== null),
            switchMap(securityInfo => {
                const formattedDate = securityInfo.passwordLastUpdate
                    ? new Date(securityInfo.passwordLastUpdate).toLocaleDateString(this.languageService.getCurrentLanguage(), {
                        year: 'numeric',
                        month: 'short',
                        day: 'numeric',
                    })
                    : null;

                const hasPasswordUpdate = !!formattedDate;

                const translationKeys = hasPasswordUpdate
                    ? ['Password', 'LastChanged', '2-StepVerification', '2FAOn', '2FAOff']
                    : ['Password', 'UnchangedPassword', '2-StepVerification', '2FAOn', '2FAOff'];

                return this.translate.get(translationKeys, { value: formattedDate }).pipe(
                    map(translations => {
                        const twoFADescription = securityInfo.twoFactorEnable
                            ? translations['2FAOn']
                            : translations['2FAOff'];

                        const passwordDescription = hasPasswordUpdate
                            ? translations['LastChanged']
                            : translations['UnchangedPassword'];

                        const res: AccordionItem[] = [
                            {
                                title: translations['2-StepVerification'],
                                titleIcon: 'pi pi-shield',
                                description: twoFADescription,
                                routerLink: '/signin-option/two-factor-verification',
                            },
                            {
                                title: translations['Password'],
                                titleIcon: 'pi pi-key',
                                description: passwordDescription,
                                routerLink: '/signin-option/password',
                            }
                        ];
                        return res;
                    })
                );
            }),
            takeUntil(this.destroy$)
        ).subscribe(result => {
            this.securityItems = result
            this.loading = false;
        })

    }
}
