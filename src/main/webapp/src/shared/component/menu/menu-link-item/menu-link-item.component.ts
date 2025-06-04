import { booleanAttribute, Component, Input } from '@angular/core';
import { NgIf } from "@angular/common";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'b-menu-link-item',
    imports: [
        NgIf,
        RouterLink
    ],
    templateUrl: './menu-link-item.component.html',
    standalone: true,
    styleUrl: './menu-link-item.component.scss'
})
export class MenuLinkItemComponent {
    @Input() item!: LinkItem;
    @Input({ transform: booleanAttribute }) isSubRender: boolean = false;
}
