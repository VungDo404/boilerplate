import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import PerfectScrollbar from "perfect-scrollbar";
import { NgForOf } from "@angular/common";
import { SidebarItemComponent } from "./sidebar-item/sidebar-item.component";
import { RouterLink } from "@angular/router";
import { LangChangeEvent, TranslatePipe, TranslateService } from "@ngx-translate/core";
import { Subject, takeUntil } from "rxjs";

@Component({
    selector: 'aside[app-sidebar]',
    imports: [
        NgForOf,
        SidebarItemComponent,
        RouterLink,
        TranslatePipe
    ],
    templateUrl: './sidebar.component.html',
    standalone: true,
    styleUrl: './sidebar.component.scss'
})
export class SidebarComponent implements OnInit, AfterViewInit, OnDestroy {
    private destroy$ = new Subject<void>();
    @ViewChild('scrollable') scrollableRef!: ElementRef;
    private ps!: PerfectScrollbar;
    items!: SettingSidebarItem[];
    constructor(private translate: TranslateService) {}
    ngAfterViewInit() {
        this.ps = new PerfectScrollbar(this.scrollableRef.nativeElement);
    }

    ngOnDestroy() {
        if (this.ps) {
            this.ps.destroy();
        }
        this.destroy$.next();
        this.destroy$.complete();
    }

    ngOnInit(): void {
        this.loadItems();
        this.translate.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.loadItems();
            });

    }

    private loadItems(){
        this.translate.get(['Account', 'Notification']).pipe(takeUntil(this.destroy$)).subscribe(translations => {
            this.items = [
                {
                    label: translations['Account'],
                    routerLink: "/account"
                },
                {
                    label: translations['Notification'],
                    routerLink: "/"
                }
            ]
        })
    }
}
