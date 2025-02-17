import { Component, OnInit } from '@angular/core';
import { Button } from "primeng/button";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { TranslatePipe } from "@ngx-translate/core";
import { ValidationMessageComponent } from "../../../shared/component/validation-message/validation-message.component";
import { NgxSpinnerService } from "ngx-spinner";
import { RegisterService } from "./register.service";
import { passwordMatchValidator } from "../../../shared/validator/confirmed-password.validator";


@Component({
    selector: 'app-register',
    imports: [
        Button,
        FormsModule,
        ReactiveFormsModule,
        TranslatePipe,
        ValidationMessageComponent,
    ],
    templateUrl: './register.component.html',
    standalone: true,
    styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {
    registerForm!: FormGroup;
    submitted = false;

    constructor(
        private formBuilder: FormBuilder,
        private spinnerService: NgxSpinnerService,
        private registerService: RegisterService,
    ) {}

    ngOnInit() {
        this.registerForm = this.formBuilder.group({
            username: ['',
                [
                    Validators.required,
                    Validators.minLength(3),
                    Validators.maxLength(50),
                    Validators.pattern(/^[\w!@#$%^&*()\-=+<>?,.;:'"{}\[\]\\\/|`~]+$/)
                ]
            ],
            email: ['',
                [
                    Validators.required,
                    Validators.minLength(3),
                    Validators.maxLength(50),
                    Validators.email
                ]
            ],
            displayName: ['',
                [
                    Validators.required,
                    Validators.minLength(2),
                    Validators.maxLength(50)
                ]
            ],
            password: ['',
                [
                    Validators.required,
                    Validators.minLength(6),
                    Validators.maxLength(60),
                    Validators.pattern(/^[\w!@#$%^&*()\-=+<>?,.;:'"{}\[\]\\\/|`~]+$/)
                ]
            ],
            confirmedPassword: ['',
                [
                    Validators.required,
                    Validators.minLength(6),
                    Validators.maxLength(60)
                ]
            ]
        },
            {validators: passwordMatchValidator}
        );
    }

    register() {
        if (this.registerForm.invalid) {
            this.registerForm.markAllAsTouched();
            return;
        }
        this.submitted = true;
        const cb = () => {
            this.submitted = false;
            this.spinnerService.hide();
        }
        const { confirmedPassword, ...rest} = this.registerForm.value as RegisterFormWithConfirmedPassword;
        this.registerService.register(rest, cb);
    }

    resetForm() {
        this.submitted = false;
        this.registerForm.reset();
    }
}
