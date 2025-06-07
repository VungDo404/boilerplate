import { Component, OnDestroy, OnInit } from '@angular/core';
import { SessionService } from "../../../service/session.service";
import { HeaderMenuService } from "../header-menu.service";
import { LangChangeEvent, TranslateService } from "@ngx-translate/core";
import { BehaviorSubject, Subject, takeUntil } from "rxjs";
import { MenuComponent } from "../../menu/menu.component";
import { AsyncPipe, NgIf } from "@angular/common";

@Component({
    selector: 'app-header-button',
    imports: [
        MenuComponent,
        AsyncPipe,
        NgIf
    ],
    templateUrl: './header-button.component.html',
    standalone: true,
    styleUrl: './header-button.component.scss'
})
export class HeaderButtonComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();
    itemSubject!: BehaviorSubject<MenuData>;
    constructor(
        private sessionService: SessionService,
        private headerMenuService: HeaderMenuService,
        private translate: TranslateService
    ) {
        this.itemSubject = this.headerMenuService.getItemSubject;
    }

    ngOnInit(): void {
        this.headerMenuService.loadMenuItems();

        this.translate.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.headerMenuService.loadMenuItems();
            });

    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
        this.headerMenuService.dispose();
    }
}
