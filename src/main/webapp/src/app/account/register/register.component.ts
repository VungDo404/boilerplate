import { Component, OnInit } from '@angular/core';
import { Button } from "primeng/button";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { TranslatePipe } from "@ngx-translate/core";
import { ValidationMessageComponent } from "../../../shared/component/validation-message/validation-message.component";
import { NgxSpinnerService } from "ngx-spinner";
import { Toast } from "primeng/toast";
import { MessageService, ToastMessageOptions } from "primeng/api";
import { RegisterService } from "./register.service";
import { Subscription } from "rxjs";


@Component({
    selector: 'app-register',
    imports: [
        Button,
        FormsModule,
        ReactiveFormsModule,
        TranslatePipe,
        ValidationMessageComponent,
        Toast
    ],
    providers: [MessageService],
    templateUrl: './register.component.html',
    standalone: true,
    styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {
    registerForm!: FormGroup;
    submitted = false;
    toastMessageOptions: ToastMessageOptions[] = [];
    private toastSubscription!: Subscription;

    constructor(
        private formBuilder: FormBuilder,
        private spinnerService: NgxSpinnerService,
        private registerService: RegisterService,
        private messageService: MessageService
    )
    {}

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
            {validators: this.passwordMatchValidator}
        );
        this.toastSubscription = this.registerService.toastMessage$.subscribe(
            option => {
                this.toastMessageOptions = option;
                this.messageService.addAll(this.toastMessageOptions);
            }
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

    passwordMatchValidator(form: FormGroup) {
        const password = form.get('password');
        const confirmedPassword = form.get('confirmedPassword');

        if (password && confirmedPassword && password.value !== confirmedPassword.value) {
            confirmedPassword.setErrors({ passwordMismatch: true });
        }
    }

    resetForm() {
        this.submitted = false;
        this.registerForm.reset();
    }
}
