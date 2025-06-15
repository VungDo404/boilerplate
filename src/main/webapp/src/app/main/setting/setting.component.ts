import { Component } from '@angular/core';
import { RouterOutlet } from "@angular/router";
import { SidebarComponent } from "../../../shared/component/sidebar/sidebar.component";

@Component({
    selector: 'app-setting',
    imports: [
        RouterOutlet,
        SidebarComponent
    ],
    templateUrl: './setting.component.html',
    standalone: true,
    styleUrl: './setting.component.scss'
})
export class SettingComponent {

}
