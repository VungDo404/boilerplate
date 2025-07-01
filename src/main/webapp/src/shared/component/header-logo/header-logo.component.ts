import { Component } from '@angular/core';
import { Image } from "primeng/image";
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-header-logo',
    imports: [
        Image,
        RouterLink
    ],
  templateUrl: './header-logo.component.html',
  standalone: true,
  styleUrl: './header-logo.component.scss'
})
export class HeaderLogoComponent {

}
