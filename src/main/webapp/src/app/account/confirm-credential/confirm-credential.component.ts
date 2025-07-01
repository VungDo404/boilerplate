import { Component, OnInit } from '@angular/core';
import { BaseComponent } from "../../../shared/component/base.component";
import { ConfirmCredentialService } from "./confirm-credential.service";
import { Action } from "../../../shared/const/app.enum";
import { Button } from "primeng/button";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { NgIf } from "@angular/common";
import { PermissionPipe } from "../../../shared/pipe/permission.pipe";
import { TranslatePipe } from "@ngx-translate/core";
import { ValidationMessageComponent } from "../../../shared/component/validation-message/validation-message.component";
import { SessionService } from "../../../shared/service/session.service";
import { BusyDirective } from "../../../shared/directive/busy.directive";
import { ActivatedRoute } from "@angular/router";

@Component({
    selector: 'app-confirm-credential',
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
    templateUrl: './confirm-credential.component.html',
    standalone: true,
    styleUrl: './confirm-credential.component.scss'
})
export class ConfirmCredentialComponent extends BaseComponent implements OnInit {
    form!: FormGroup;
    loading= false;
    constructor(
        private formBuilder: FormBuilder,
        private confirmCredentialService: ConfirmCredentialService,
        private sessionService: SessionService,
        private route: ActivatedRoute
    ) {super();}

    submit() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.loading = true;
        const cb = () => {
            this.loading = false;
        }
        this.route.queryParamMap.subscribe(params => {
            const redirect = params.get('redirect') ?? '/';
            this.confirmCredentialService.confirmCredential(this.form.getRawValue(), cb, redirect);
        })
    }

    protected readonly Action = Action;

    ngOnInit(): void {
        this.form = this.formBuilder.group({
            username: [
                { value: this.sessionService.username, disabled: true },
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

    get userId(){
        return this.sessionService.id;
    }

}
