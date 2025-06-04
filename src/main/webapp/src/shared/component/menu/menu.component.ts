import { Component, ElementRef, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { NgForOf, NgIf } from "@angular/common";
import { MenuProfileItemComponent } from "./menu-profile-item/menu-profile-item.component";
import { MenuLinkItemComponent } from "./menu-link-item/menu-link-item.component";
import { MenuService } from "./menu.service";

@Component({
    selector: 'b-menu',
    imports: [
        NgIf,
        NgForOf,
        MenuProfileItemComponent,
        MenuLinkItemComponent
    ],
    templateUrl: './menu.component.html',
    standalone: true,
    styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnDestroy, OnInit, OnChanges {
    @ViewChild('menu') menuRef!: ElementRef;
    @Input() items!: MenuData;
    @Input() currentMenu!: keyof MenuData;

    position = { top: 0, left: 0 };
    private resizeListener?: (event: Event) => void;
    private clickListener?: (event: Event) => void;

    private readonly SPACING = 8;

    constructor(protected menuService: MenuService) { }

    toggle(event: Event) {
        if (this.menuService.isVisible) {
            this.hide();
            this.menuService.hide();
        } else {
            this.menuService.show();
            this.show(event);
        }
    }

    protected get getCurrentMenuData() {
        return this.items[this.currentMenu];
    }

    private navigateToSubmenu(item: LinkItem) {
        if (item.target) {
            this.menuService.navigateToSubmenu(item.target);
        }
    }

    protected navigateBack() {
        this.menuService.navigateBack();
    }

    private show(event: Event) {
        setTimeout(() => {
            this.updatePosition(event.target as HTMLElement);
        }, 0);
        this.addClickOutsideListener();
        this.addResizeListener(event.target as HTMLElement);
    }

    private hide() {
        this.removeClickOutsideListener();
        this.removeResizeListener();
    }

    private updatePosition(target: HTMLElement) {
        const rect = target.getBoundingClientRect();
        const menu = this.menuRef.nativeElement as HTMLElement;
        const menuRect = menu.getBoundingClientRect();

        this.position = {
            top: rect.top + window.scrollY,
            left: rect.left + window.scrollX - menuRect.width - this.SPACING
        };
    }

    private addResizeListener(target: HTMLElement) {
        this.resizeListener = () => this.updatePosition(target);
        window.addEventListener('resize', this.resizeListener);
    }

    private removeResizeListener() {
        if (this.resizeListener) {
            window.removeEventListener('resize', this.resizeListener);
            this.resizeListener = undefined;
        }
    }

    private addClickOutsideListener() {
        this.removeClickOutsideListener();
        this.clickListener = (event: Event) => {
            const target = event.target as HTMLElement;
            const menu = this.menuRef?.nativeElement as HTMLElement;

            if (menu && !menu.contains(target)) {
                this.hide();
            }
        }
        setTimeout(() => {
            document.addEventListener('click', this.clickListener!);
        }, 0)
    }

    private removeClickOutsideListener() {
        if (this.clickListener) {
            document.removeEventListener('click', this.clickListener);
            this.clickListener = undefined;
        }
    }

    private addSubMenuCallBack() {
        for (const key in this.items) {
            const menuSection = this.items[key as keyof MenuData];
            for (const section of menuSection.sections) {
                for (const item of section.items) {
                    if (item.type === 'link' && item.target && !item.cb) {
                        item.cb = this.navigateToSubmenu.bind(this);
                    }
                }
            }
        }
    }

    ngOnDestroy(): void {
        this.hide();
    }

    ngOnInit(): void {
        this.addSubMenuCallBack();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if(changes['items']){
            this.addSubMenuCallBack();
        }
    }
}
