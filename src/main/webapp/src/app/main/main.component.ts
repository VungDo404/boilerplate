import { Component, OnInit } from '@angular/core';
import { HeaderComponent } from "../../shared/component/header/header.component";
import { HeaderLogoComponent } from "../../shared/component/header-logo/header-logo.component";
import { RouterOutlet } from "@angular/router";

@Component({
  selector: 'app-main',
    imports: [
        HeaderComponent,
        HeaderLogoComponent,
        RouterOutlet
    ],
  templateUrl: './main.component.html',
  standalone: true,
  styleUrl: './main.component.scss'
})
export class MainComponent{


}
