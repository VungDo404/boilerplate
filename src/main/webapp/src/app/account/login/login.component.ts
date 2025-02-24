import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { Button } from "primeng/button";
import { TranslatePipe } from "@ngx-translate/core";
import { ValidationMessageComponent } from "../../../shared/component/validation-message/validation-message.component";
import { LoginService } from "./login.service";
import { NgxSpinnerService } from "ngx-spinner";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'app-login',
    imports: [
        FormsModule,
        Button,
        TranslatePipe,
        ReactiveFormsModule,
        ValidationMessageComponent,
        RouterLink,
    ],
    templateUrl: './login.component.html',
    standalone: true,
    styleUrl: './login.component.scss'
})
export class LoginComponent implements OnDestroy, OnInit {
    submitted = false;

    constructor(
        protected loginService: LoginService,
        private spinnerService: NgxSpinnerService,
    ) {}

    get loginForm() {
        return this.loginService.loginForm
    }

    login() {
        if (this.loginService.loginForm.invalid) {
            this.loginService.loginForm.markAllAsTouched();
            return;
        }
        this.submitted = true;
        this.spinnerService.show();
        const cb = () => {
            this.submitted = false;
            this.spinnerService.hide();
        }
        this.loginService.authenticate(cb);

    }

    ngOnDestroy(): void {
        this.resetForm();
    }

    resetForm() {
        this.submitted = false;
    }

    ngOnInit(): void {
        this.loginService.initForm()
    }
}
