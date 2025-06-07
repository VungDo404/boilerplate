import { Component, Input } from '@angular/core';
import { Avatar } from "primeng/avatar";
import { NgIf } from "@angular/common";
import { PermissionPipe } from "../../../pipe/permission.pipe";

@Component({
    selector: 'b-menu-profile-item',
    imports: [
        Avatar,
        NgIf,
        PermissionPipe
    ],
    templateUrl: './menu-profile-item.component.html',
    standalone: true,
    styleUrl: './menu-profile-item.component.scss'
})
export class MenuProfileItemComponent {
    @Input() item!: ProfileItem;
}
