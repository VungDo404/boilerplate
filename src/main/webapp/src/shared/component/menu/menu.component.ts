import {
    ChangeDetectorRef,
    Component,
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
    ViewChild
} from '@angular/core';
import { NgForOf, NgIf } from "@angular/common";
import { MenuProfileItemComponent } from "./menu-profile-item/menu-profile-item.component";
import { MenuLinkItemComponent } from "./menu-link-item/menu-link-item.component";
import { MenuService } from "./menu.service";
import { MenuDirective } from "../../directive/menu.directive";

@Component({
    selector: 'b-menu',
    imports: [
        NgIf,
        NgForOf,
        MenuProfileItemComponent,
        MenuLinkItemComponent,
        MenuDirective
    ],
    templateUrl: './menu.component.html',
    standalone: true,
    styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit, OnChanges {
    @ViewChild('menuEl', { static: false }) menuDirective!: MenuDirective;
    @Input() items!: MenuData;
    @Input() currentMenu!: keyof MenuData;

    position = { top: 0, left: 0 };

    constructor(protected menuService: MenuService, private changeDetectorRef: ChangeDetectorRef) { }

    toggle(event: Event) {
        event.stopPropagation();
        if (this.menuService.isVisible) {
            this.onHide();
        } else {
            this.menuService.show();
            this.changeDetectorRef.detectChanges();
            this.menuDirective.show(event.currentTarget as HTMLElement);
        }
    }

    onPositionChange(pos: { top: number; left: number }) {
        this.position = pos;
    }

    onHide(){
        this.menuService.hide();
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

    protected get isVisible(){
        return this.menuService.isVisible
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

    ngOnInit(): void {
        this.addSubMenuCallBack();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if(changes['items']){
            this.addSubMenuCallBack();
        }
    }
}
