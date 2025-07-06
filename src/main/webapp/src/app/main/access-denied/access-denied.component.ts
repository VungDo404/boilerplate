import { Component } from '@angular/core';
import { RouterLink } from "@angular/router";
import { Button } from "primeng/button";
import { TranslatePipe } from "@ngx-translate/core";
import { Image } from "primeng/image";

@Component({
  selector: 'app-access-denied',
  imports: [
    RouterLink,
    Button,
    TranslatePipe,
    Image
  ],
  templateUrl: './access-denied.component.html',
  standalone: true,
  styleUrl: './access-denied.component.scss'
})
export class AccessDeniedComponent {

}
