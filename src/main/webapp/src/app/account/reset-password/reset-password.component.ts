import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { passwordMatchValidator } from "../../../shared/validator/confirmed-password.validator";
import { ResetPasswordService } from "./reset-password.service";
import { ActivatedRoute } from "@angular/router";
import { Button } from "primeng/button";
import { TranslatePipe } from "@ngx-translate/core";
import { ValidationMessageComponent } from "../../../shared/component/validation-message/validation-message.component";
import { ROOT_OBJECT } from "../../../shared/const/app.const";
import { Action } from "../../../shared/const/app.enum";
import { NgIf } from "@angular/common";
import { PermissionPipe } from "../../../shared/pipe/permission.pipe";
import { PasswordToggleDirective } from "../../../shared/directive/password-toggle.directive";

@Component({
    selector: 'app-reset-password',
    imports: [
        Button,
        FormsModule,
        ReactiveFormsModule,
        TranslatePipe,
        ValidationMessageComponent,
        NgIf,
        PermissionPipe,
        PasswordToggleDirective
    ],
    templateUrl: './reset-password.component.html',
    standalone: true,
    styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent {
    form!: FormGroup;

    constructor(
        private formBuilder: FormBuilder,
        private spinnerService: NgxSpinnerService,
        private resetPasswordService: ResetPasswordService,
        private route: ActivatedRoute
    ) {
        this.form = this.formBuilder.group({
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
            { validators: passwordMatchValidator }
        );
    }

    resetPassword() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.spinnerService.show();
        const cb = () => {
            this.spinnerService.hide();
        }
        this.route.queryParamMap.subscribe(params => {
            const key = params.get('key') || '';
            const password = this.form.value.password;
            this.resetPasswordService.resetPassword(password, key, cb);
        });

    }

    protected readonly ROOT_OBJECT = ROOT_OBJECT;
    protected readonly Action = Action;
}
