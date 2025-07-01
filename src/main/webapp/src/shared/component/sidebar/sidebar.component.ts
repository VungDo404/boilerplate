import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import PerfectScrollbar from "perfect-scrollbar";
import { NgForOf, NgIf } from "@angular/common";
import { SidebarItemComponent } from "./sidebar-item/sidebar-item.component";
import { RouterLink } from "@angular/router";
import { LangChangeEvent, TranslatePipe, TranslateService } from "@ngx-translate/core";
import { takeUntil } from "rxjs";
import { BaseComponent } from "../base.component";
import { SessionService } from "../../service/session.service";

@Component({
    selector: 'aside[app-sidebar]',
    imports: [
        NgForOf,
        SidebarItemComponent,
        RouterLink,
        TranslatePipe,
        NgIf
    ],
    templateUrl: './sidebar.component.html',
    standalone: true,
    styleUrl: './sidebar.component.scss'
})
export class SidebarComponent extends BaseComponent implements OnInit, AfterViewInit {
    @ViewChild('scrollable') scrollableRef!: ElementRef;
    private ps!: PerfectScrollbar;
    items!: SettingSidebarItem[];
    constructor(private translate: TranslateService, private sessionService: SessionService) {super();}
    ngAfterViewInit() {
        this.ps = new PerfectScrollbar(this.scrollableRef.nativeElement);
    }


    override ngOnDestroy() {
        super.ngOnDestroy()
        if (this.ps) {
            this.ps.destroy();
        }
    }

    ngOnInit(): void {
        this.loadItems();
        this.translate.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.loadItems();
            });

    }

    get provider(){
        return this.sessionService.provider;
    }

    private loadItems(){
        this.translate.get(['Account', 'Notification', 'Security']).pipe(takeUntil(this.destroy$)).subscribe(translations => {
            this.items = [
                {
                    label: translations['Account'],
                    routerLink: "/account",
                    isRender: true
                },
                {
                    label: translations['Security'],
                    routerLink: "/security",
                    isRender: this.provider !== 'LOCAL'
                },
                {
                    label: translations['Notification'],
                    routerLink: "/",
                    isRender: true
                }
            ]
        })
    }
}
