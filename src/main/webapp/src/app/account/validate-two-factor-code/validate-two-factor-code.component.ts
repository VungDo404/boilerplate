import { Component, OnInit } from '@angular/core';
import { TranslatePipe } from "@ngx-translate/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { InputOtp } from "primeng/inputotp";
import { NgIf } from "@angular/common";
import { Button } from "primeng/button";
import { Router } from "@angular/router";
import { ValidateTwoFactorCodeService } from "./validate-two-factor-code.service";
import { ValidationMessageComponent } from "../../../shared/component/validation-message/validation-message.component";
import { InputText } from "primeng/inputtext";
import { NgxSpinnerService } from "ngx-spinner";
import { LoginService } from "../login/login.service";

@Component({
    selector: 'app-validate-two-factor-code',
    imports: [
        TranslatePipe,
        FormsModule,
        InputOtp,
        NgIf,
        Button,
        ReactiveFormsModule,
        ValidationMessageComponent,
        InputText
    ],
    templateUrl: './validate-two-factor-code.component.html',
    standalone: true,
    styleUrl: './validate-two-factor-code.component.scss'
})
export class ValidateTwoFactorCodeComponent implements OnInit {
    form!: FormGroup;

    constructor(
        private router: Router,
        private validateTwoFactorCodeService: ValidateTwoFactorCodeService,
        private formBuilder: FormBuilder,
        private spinnerService: NgxSpinnerService,
        private loginService: LoginService
    ) {}

    submit() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.spinnerService.show();
        const cb = () => {
            this.spinnerService.hide();
        }
        this.validateTwoFactorCodeService.validate(this.form.value.otp, cb);
    }

    private get canActive(){
        return !!(this.loginService.loginForm.get('username') && this.loginService.loginForm.get('password'));
    }

    ngOnInit(): void {
        if (!this.canActive)
            this.router.navigate(["account/login"]);
        this.form = this.formBuilder.group({
            otp: [
                '',
                [
                    Validators.required,
                    Validators.minLength(6),
                    Validators.pattern('^[0-9]*$')
                ]
            ]
        });
    }
}
