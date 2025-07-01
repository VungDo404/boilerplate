import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { passwordMatchValidator } from "../../../../shared/validator/confirmed-password.validator";
import { ROOT_OBJECT } from "../../../../shared/const/app.const";
import { Action } from "../../../../shared/const/app.enum";
import { Button } from "primeng/button";
import { NgIf } from "@angular/common";
import { PermissionPipe } from "../../../../shared/pipe/permission.pipe";
import { TranslatePipe } from "@ngx-translate/core";
import {
    ValidationMessageComponent
} from "../../../../shared/component/validation-message/validation-message.component";
import { SessionService } from "../../../../shared/service/session.service";
import { PasswordService } from "./password.service";
import { BusyDirective } from "../../../../shared/directive/busy.directive";

@Component({
    selector: 'app-password',
    imports: [
        Button,
        FormsModule,
        NgIf,
        PermissionPipe,
        ReactiveFormsModule,
        TranslatePipe,
        ValidationMessageComponent,
        BusyDirective
    ],
    templateUrl: './password.component.html',
    standalone: true,
    styleUrl: './password.component.scss'
})
export class PasswordComponent implements OnInit{
    form!: FormGroup;
    loading = false;
    constructor(private formBuilder: FormBuilder, private sessionService: SessionService, private passwordService: PasswordService) {}

    ngOnInit(): void {
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
            {validators: passwordMatchValidator}
        );
    }

    submit(){
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.loading = true;
        const cb = () => {
            this.loading = false;
        }
        const body: ChangePassword = {
            id: this.id,
            newPassword: this.form.get('password')!.value
        }
        this.passwordService.changePassword(body, cb);
    }

    get id(){
        return this.sessionService.id;
    }

    protected readonly ROOT_OBJECT = ROOT_OBJECT;
    protected readonly Action = Action;
}
