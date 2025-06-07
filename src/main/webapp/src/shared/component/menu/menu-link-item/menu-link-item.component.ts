import { booleanAttribute, Component, Input } from '@angular/core';
import { NgIf } from "@angular/common";
import { RouterLink } from "@angular/router";
import { PermissionPipe } from "../../../pipe/permission.pipe";

@Component({
    selector: 'b-menu-link-item',
    imports: [
        NgIf,
        RouterLink,
        PermissionPipe
    ],
    templateUrl: './menu-link-item.component.html',
    standalone: true,
    styleUrl: './menu-link-item.component.scss'
})
export class MenuLinkItemComponent {
    @Input() item!: LinkItem;
    @Input({ transform: booleanAttribute }) isSubRender: boolean = false;

}
