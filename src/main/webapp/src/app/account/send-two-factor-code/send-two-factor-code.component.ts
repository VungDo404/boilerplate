import { Component, OnInit } from '@angular/core';
import { TranslatePipe } from "@ngx-translate/core";
import { Router } from "@angular/router";
import { Select } from "primeng/select";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { SendTwoFactorCodeService } from "./send-two-factor-code.service";
import { Button } from "primeng/button";

@Component({
    selector: 'app-send-two-factor-code',
    imports: [
        TranslatePipe,
        Select,
        FormsModule,
        ReactiveFormsModule,
        Button
    ],
    templateUrl: './send-two-factor-code.component.html',
    standalone: true,
    styleUrl: './send-two-factor-code.component.scss'
})
export class SendTwoFactorCodeComponent implements OnInit {
    form!: FormGroup;
    loginResult!: RequireTwoFactorAuthenticationResult;
    selectedOption!: { label: string, value: string }[];

    constructor(
        private router: Router,
        private formBuilder: FormBuilder,
        private spinnerService: NgxSpinnerService,
        private sendTwoFactorCodeService: SendTwoFactorCodeService
    ) {
        const navigation = this.router.getCurrentNavigation();
        this.loginResult = navigation?.extras.state?.loginResult || null;
        this.selectedOption = this.loginResult.twoFactorProviders.map(p => ({ value: p, label: this.formatLabel(p) }));
    }

    ngOnInit(): void {
        if (!this.canActive)
            this.router.navigate(["/account/login"]);
        this.form = this.formBuilder.group({
            provider: [
                '',
                [
                    Validators.required
                ]
            ]
        });
        this.form.controls['provider'].setValue(this.selectedOption[0].value);

    }

    submit() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.spinnerService.show();
        const cb = () => {
            this.spinnerService.hide();
        }
        const model = {userId: this.loginResult.userId, provider: this.form.value.provider}
        this.sendTwoFactorCodeService.sendTwoFactorCode(model,cb);

    }

    private formatLabel(value: string): string {
        return value
            .toLowerCase()
            .replace(/_/g, ' ')
            .replace(/\b\w/g, char => char.toUpperCase());
    }

    private get canActive() {
        return Boolean(this.loginResult)
    }
}
