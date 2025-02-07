import { Component } from '@angular/core';
import { FormsModule } from "@angular/forms";
import { Button } from "primeng/button";
import { TranslatePipe } from "@ngx-translate/core";

@Component({
    selector: 'app-login',
    imports: [
        FormsModule,
        Button,
        TranslatePipe,
    ],
    templateUrl: './login.component.html',
    standalone: true,
    styleUrl: './login.component.scss'
})
export class LoginComponent {
    private submitting = false;

    login() {

    }
}
