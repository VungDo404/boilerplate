import { Component } from '@angular/core';
import { TranslatePipe } from "@ngx-translate/core";

@Component({
  selector: 'app-search',
  imports: [
    TranslatePipe
  ],
  templateUrl: './header-search.component.html',
  standalone: true,
  styleUrl: './header-search.component.scss'
})
export class HeaderSearchComponent {

}
