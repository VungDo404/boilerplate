import { Component, OnDestroy, OnInit } from '@angular/core';
import { Avatar } from "primeng/avatar";
import { MenuComponent } from "../../menu/menu.component";
import { SessionService } from "../../../service/session.service";
import { LangChangeEvent, TranslateService } from "@ngx-translate/core";
import { BehaviorSubject, Observable, Subject, takeUntil } from "rxjs";
import { AsyncPipe, NgIf } from "@angular/common";
import { HeaderMenuService } from "../header-menu.service";
import { BaseComponent } from "../../base.component";

@Component({
    selector: 'app-header-avatar',
    imports: [
        Avatar,
        MenuComponent,
        AsyncPipe,
        NgIf
    ],
    templateUrl: './header-avatar.component.html',
    standalone: true,
    styleUrl: './header-avatar.component.scss'
})
export class HeaderAvatarComponent extends BaseComponent implements OnInit {
    avatar$!: Observable<string>;
    itemSubject!: BehaviorSubject<MenuData>;
    constructor(
        private sessionService: SessionService,
        private headerMenuService: HeaderMenuService,
        private translate: TranslateService
    ) {
        super();
        this.avatar$ = this.sessionService.avatar$;
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


    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.headerMenuService.dispose();
    }
}
