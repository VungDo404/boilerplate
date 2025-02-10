import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { Button } from "primeng/button";
import { TranslatePipe } from "@ngx-translate/core";
import { NgIf } from "@angular/common";
import { ValidationMessageComponent } from "../../../shared/component/validation-message/validation-message.component";
import { LoginService } from "./login.service";
import { NgxSpinnerService } from "ngx-spinner";

export interface LoginForm {
    username: string;
    password: string;
}


@Component({
    selector: 'app-login',
    imports: [
        FormsModule,
        Button,
        TranslatePipe,
        ReactiveFormsModule,
        ValidationMessageComponent,
    ],
    templateUrl: './login.component.html',
    standalone: true,
    styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
    loginForm!: FormGroup;
    submitted = false;

    constructor(private formBuilder: FormBuilder, private loginService: LoginService, private spinnerService: NgxSpinnerService) {}

    ngOnInit() {
        this.loginForm = this.formBuilder.group({
            username: [
                '',
                [
                    Validators.required,
                    Validators.minLength(3),
                    Validators.maxLength(50),
                    Validators.pattern(/^[\w!@#$%^&*()\-=+<>?,.;:'"{}\[\]\\\/|`~]+$/)
                ]
            ],
            password: [
                '',
                [
                    Validators.required,
                    Validators.minLength(6),
                    Validators.maxLength(60),
                    Validators.pattern(/^[\w!@#$%^&*()\-=+<>?,.;:'"{}\[\]\\\/|`~]+$/)
                ]
            ]
        });
    }

    login() {
        if (this.loginForm.invalid) {
            this.loginForm.markAllAsTouched();
            return;
        }

        this.submitted = true;
        this.spinnerService.show();
        const cb = () => {
            this.submitted = false;
            this.spinnerService.hide();
        }
        this.loginService.authenticate(this.loginForm.value, cb);

    }


    resetForm() {
        this.submitted = false;
        this.loginForm.reset();
    }
}
