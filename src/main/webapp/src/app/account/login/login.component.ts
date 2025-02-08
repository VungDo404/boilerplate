import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { Button } from "primeng/button";
import { TranslatePipe } from "@ngx-translate/core";
import { NgIf } from "@angular/common";
import { ValidationMessageComponent } from "../../../shared/component/validation-message/validation-message.component";

@Component({
    selector: 'app-login',
    imports: [
        FormsModule,
        Button,
        TranslatePipe,
        NgIf,
        ReactiveFormsModule,
        ValidationMessageComponent,
    ],
    templateUrl: './login.component.html',
    standalone: true,
    styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit{
    loginForm!: FormGroup;
    submitted = false;

    constructor(private formBuilder: FormBuilder) {}

    ngOnInit() {
        this.loginForm = this.formBuilder.group({
            username: ['', [Validators.required, Validators.minLength(3)]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    login() {
        this.submitted = true;
        if (this.loginForm.invalid) {
            this.loginForm.markAllAsTouched();
            return;
        }
    }


    resetForm() {
        this.submitted = false;
        this.loginForm.reset();
    }
}
