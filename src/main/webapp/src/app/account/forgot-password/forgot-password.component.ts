import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { TranslatePipe } from "@ngx-translate/core";
import { ValidationMessageComponent } from "../../../shared/component/validation-message/validation-message.component";
import { Button } from "primeng/button";
import { NgxSpinnerService } from "ngx-spinner";
import { ForgotPasswordService } from "./forgot-password.service";
import { ROOT_OBJECT } from "../../../shared/const/app.const";
import { Action } from "../../../shared/const/app.enum";
import { NgIf } from "@angular/common";
import { PermissionPipe } from "../../../shared/pipe/permission.pipe";

@Component({
    selector: 'app-forgot-password',
    imports: [
        FormsModule,
        ReactiveFormsModule,
        TranslatePipe,
        ValidationMessageComponent,
        Button,
        NgIf,
        PermissionPipe
    ],
    templateUrl: './forgot-password.component.html',
    standalone: true,
    styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent {
    form!: FormGroup;

    constructor(
        private formBuilder: FormBuilder,
        private spinnerService: NgxSpinnerService,
        private forgotPasswordService: ForgotPasswordService
    ) {
        this.form = this.formBuilder.group({
            email: [
                '',
                [
                    Validators.required,
                    Validators.minLength(3),
                    Validators.maxLength(50),
                    Validators.email
                ]
            ]
        });
    }

    forgotPassword() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        const email = this.form.value.email;
        this.spinnerService.show();
        const cb = () => {
            this.spinnerService.hide();
        }
        this.forgotPasswordService.forgotPassword(email, cb);
    }

    protected readonly ROOT_OBJECT = ROOT_OBJECT;
    protected readonly Action = Action;
}
