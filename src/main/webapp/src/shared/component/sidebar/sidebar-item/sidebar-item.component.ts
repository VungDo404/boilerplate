import { Component, Input } from '@angular/core';
import { Router, RouterLink } from "@angular/router";
import { NgIf } from "@angular/common";

@Component({
    selector: 'app-sidebar-item',
    imports: [
        RouterLink,
        NgIf
    ],
    templateUrl: './sidebar-item.component.html',
    standalone: true,
    styleUrl: './sidebar-item.component.scss'
})
export class SidebarItemComponent {
    @Input() item!: SettingSidebarItem;
    constructor(public router: Router) {}

    isActive(): boolean {
        return this.router.url === this.item.routerLink;
    }
}
