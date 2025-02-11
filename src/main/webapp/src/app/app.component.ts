import { Component } from '@angular/core';
import { RouterOutlet } from "@angular/router";
import { NgxSpinnerComponent } from "ngx-spinner";
import { ToastModule } from "primeng/toast";

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, NgxSpinnerComponent],
    templateUrl: './app.component.html',
    standalone: true,
    styleUrl: './app.component.scss'
})
export class AppComponent {
    constructor() {
    }

}
