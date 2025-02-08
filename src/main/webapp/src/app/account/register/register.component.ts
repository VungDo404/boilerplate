import { Component, OnInit } from '@angular/core';
import { Button } from "primeng/button";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { TranslatePipe } from "@ngx-translate/core";
import { ValidationMessageComponent } from "../../../shared/component/validation-message/validation-message.component";

@Component({
	selector: 'app-register',
    imports: [
        Button,
        FormsModule,
        ReactiveFormsModule,
        TranslatePipe,
        ValidationMessageComponent
    ],
	templateUrl: './register.component.html',
	standalone: true,
	styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit{
    registerForm!: FormGroup;
    submitted = false;
    constructor(private formBuilder: FormBuilder) {}
    ngOnInit() {
        this.registerForm = this.formBuilder.group({
            username: ['', [Validators.required, Validators.minLength(3)]],
            email: ['', [Validators.required, Validators.minLength(3), Validators.email]],
            displayName: ['', [Validators.required, Validators.minLength(3)]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            confirmedPassword: ['', [Validators.required, Validators.minLength(6)]]
        });
    }
	register(){
        this.submitted = true;
        if (this.registerForm.invalid) {
            this.registerForm.markAllAsTouched();
            console.log(this.registerForm)
            return;
        }
	}
    resetForm() {
        this.submitted = false;
        this.registerForm.reset();
    }
}
