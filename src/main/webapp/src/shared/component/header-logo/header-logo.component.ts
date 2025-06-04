import { Component } from '@angular/core';
import { Image } from "primeng/image";

@Component({
  selector: 'app-header-logo',
  imports: [
    Image
  ],
  templateUrl: './header-logo.component.html',
  standalone: true,
  styleUrl: './header-logo.component.scss'
})
export class HeaderLogoComponent {

}
