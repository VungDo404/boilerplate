import { Component, Input } from '@angular/core';
import { Avatar } from "primeng/avatar";

@Component({
    selector: 'b-menu-profile-item',
    imports: [
        Avatar
    ],
    templateUrl: './menu-profile-item.component.html',
    standalone: true,
    styleUrl: './menu-profile-item.component.scss'
})
export class MenuProfileItemComponent {
    @Input() item!: ProfileItem;
}
