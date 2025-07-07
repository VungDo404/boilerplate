import { Component, OnInit } from '@angular/core';
import { TranslatePipe, TranslateService } from "@ngx-translate/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { NgxSpinnerComponent, NgxSpinnerService } from "ngx-spinner";
import { AccountService } from "./account.service";
import { dateOfBirthValidator } from "../../../../shared/validator/date-of-birth.validator";
import { Button } from "primeng/button";
import { Action } from "../../../../shared/const/app.enum";
import { NgForOf, NgIf } from "@angular/common";
import { PermissionPipe } from "../../../../shared/pipe/permission.pipe";
import { SessionService } from "../../../../shared/service/session.service";
import { Avatar } from "primeng/avatar";
import { UploadComponent } from "../../../../shared/component/upload/upload.component";
import {
    ValidationMessageComponent
} from "../../../../shared/component/validation-message/validation-message.component";
import { DatePicker } from "primeng/datepicker";
import { RadioButton } from "primeng/radiobutton";
import _ from 'lodash';
import { ToastService } from "../../../../shared/service/toast.service";
import { BaseComponent } from "../../../../shared/component/base.component";
import { takeUntil } from "rxjs";

@Component({
    selector: 'app-account',
    imports: [
        TranslatePipe,
        Button,
        FormsModule,
        ReactiveFormsModule,
        NgIf,
        PermissionPipe,
        Avatar,
        UploadComponent,
        ValidationMessageComponent,
        DatePicker,
        RadioButton,
        NgForOf,
        NgxSpinnerComponent,
    ],
    templateUrl: './account.component.html',
    standalone: true,
    styleUrl: './account.component.scss'
})
export class AccountComponent extends BaseComponent implements OnInit {
    form!: FormGroup;
    avatar!: string;
    submitted = false;
    spinnerName = 'account-spinner';
    protected maxDate = new Date();
    protected sex = [
        { name: 'Male', key: 0 },
        { name: 'Female', key: 1 }
    ]
    private avatarFile!: File | undefined;

    constructor(
        private formBuilder: FormBuilder,
        private spinnerService: NgxSpinnerService,
        private accountService: AccountService,
        private sessionService: SessionService,
        private toastService: ToastService,
        private translate: TranslateService
    ) {super();}

    ngOnInit(): void {
        this.form = this.formBuilder.group({
                phoneNumber: ['',
                    [
                        Validators.minLength(2),
                        Validators.maxLength(20),
                        Validators.pattern(/^\+?[1-9]\d{1,14}$/)
                    ]
                ],
                gender: [null,
                    [
                        Validators.pattern(/^[01]$/),
                    ]
                ],
                displayName: ['',
                    [
                        Validators.minLength(2),
                        Validators.maxLength(50)
                    ]
                ],
                dateOfBirth: ['',
                    [
                        dateOfBirthValidator
                    ]
                ]
            }
        );
        this.accountService.addFormInitialValue(this.setCurrentAccount.bind(this));

    }

    private setCurrentAccount(phoneNumber: string, gender: number | null, displayName: string, dateOfBirth: Date | null) {
        this.form.patchValue({
            displayName: displayName,
            gender: gender,
            phoneNumber: phoneNumber,
            dateOfBirth: dateOfBirth
        });
        this.avatar = this.sessionService.avatar;
    }

    cancel() {
        this.setCurrentAccount(
            this.accountService.phoneNumber,
            this.accountService.gender,
            this.accountService.displayName,
            this.accountService.dateOfBirth
        );
    }

    onAvatarUpload(files: File[]) {
        if (files.length >= 1) {
            const file = files[0];
            this.avatar = URL.createObjectURL(file);
            this.avatarFile = file;
        }

    }

    submit() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.submitted = true;
        this.spinnerService.show(this.spinnerName);

        const cleaned = _.pickBy(this.form.value, value => value !== null && value !== '') as UpdateUserInfo;
        const cb = () => {
            this.submitted = false;
            this.spinnerService.hide(this.spinnerName);
            this.translate.get(['Success', 'UpdateAccountSuccessMessage'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.toastService.push('success', translations['Success'], translations['UpdateAccountSuccessMessage'], 'top-center')
                })
        }
        this.accountService.updateUserInfo(
            cleaned,
            cb,
            this.avatarFile ?? undefined
        )
        this.avatarFile = undefined;
    }

    get userId() {
        return this.sessionService.id;
    }

    protected readonly Action = Action;
}
