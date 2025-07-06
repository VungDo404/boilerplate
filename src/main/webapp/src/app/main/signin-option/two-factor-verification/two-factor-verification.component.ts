import { Component, OnInit, ViewChild } from '@angular/core';
import { LangChangeEvent, TranslatePipe, TranslateService } from "@ngx-translate/core";
import { Button } from "primeng/button";
import { SecurityService } from "../../setting/security/security.service";
import { AccordionComponent, AccordionItem } from "../../../../shared/component/accordion/accordion.component";
import { SessionService } from "../../../../shared/service/session.service";
import { TwoFactorVerificationService } from "./two-factor-verification.service";
import { AccordionItemComponent } from "../../../../shared/component/accordion/accordion-item/accordion-item.component";
import { NgIf } from "@angular/common";
import { Image } from "primeng/image";
import { Dialog } from "primeng/dialog";
import { QRCodeComponent } from "angularx-qrcode";
import { BaseComponent } from "../../../../shared/component/base.component";
import { takeUntil } from "rxjs";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { Action } from "../../../../shared/const/app.enum";
import { PermissionPipe } from "../../../../shared/pipe/permission.pipe";
import { InputOtp } from "primeng/inputotp";
import { ToastService } from "../../../../shared/service/toast.service";
import { LanguageService } from "../../../../shared/service/language.service";
import { BusyDirective } from "../../../../shared/directive/busy.directive";

@Component({
    selector: 'app-two-factor-verification',
    imports: [
        TranslatePipe,
        Button,
        AccordionComponent,
        AccordionItemComponent,
        NgIf,
        Image,
        Dialog,
        QRCodeComponent,
        PermissionPipe,
        ReactiveFormsModule,
        FormsModule,
        InputOtp,
        BusyDirective
    ],
    templateUrl: './two-factor-verification.component.html',
    standalone: true,
    styleUrl: './two-factor-verification.component.scss'
})
export class TwoFactorVerificationComponent extends BaseComponent implements OnInit {
    @ViewChild('otpInput') otpInput!: InputOtp;
    buttonText!: string;
    items!: AccordionItem[];
    twoFactorInfo!: TwoFactorInfo;
    authenticatorInfo!: AuthenticatorInfo;
    translatedMessage: string = '';
    dialogVisibility: BooleanMap = {
        turnOn2FA: false,
        turnOff2FA: false,
        setUpAuthenticator: false,
        verifyCode: false,
        disableAuthenticator: false
    }
    loadingQRModal = true;
    form!: FormGroup;

    constructor(
        private securityService: SecurityService,
        private sessionService: SessionService,
        private factorVerificationService: TwoFactorVerificationService,
        private translate: TranslateService,
        private formBuilder: FormBuilder,
        private toastService: ToastService,
        private languageService: LanguageService
    ) {super();}

    ngOnInit(): void {
        this.loadData();
        this.handleTranslateMessage();
        this.translate.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.handleTranslateMessage();
                this.setData();
            });
        this.initForm();
    }

    handleTwoFactor() {
        if (!this.twoFactorInfo.twoFactorEnable) {
            const cb = () => {
                this.dialogVisibility.turnOn2FA = true;
                this.twoFactorInfo.twoFactorEnable = true;
                this.buttonText = "Turn2FAOff";
            }
            this.factorVerificationService.enable2FA(cb);
        } else {
            this.dialogVisibility.turnOff2FA = true;
        }
    }

    handleDisable() {
        if (this.twoFactorInfo.twoFactorEnable) {
            const cb = () => {
                this.twoFactorInfo.twoFactorEnable = false;
                this.buttonText = "Turn2FAOn";
                this.dialogVisibility.turnOff2FA = false;
            }
            this.factorVerificationService.disable(cb);
        }
    }

    navigateVerify() {
        this.dialogVisibility.setUpAuthenticator = false;
        this.dialogVisibility.verifyCode = true;
    }

    navigateBackSetUp() {
        this.dialogVisibility.setUpAuthenticator = true;
        this.dialogVisibility.verifyCode = false;
    }

    openSetUpModal() {
        this.dialogVisibility.setUpAuthenticator = true;
        this.factorVerificationService.getAuthenticatorInfo().subscribe({
            next: (response) => {
                this.loadingQRModal = false;
                setTimeout(() => {this.authenticatorInfo = response;}, 1000)
            }
        });
    }

    resetVerifyForm(): void {
        this.form.reset();
        this.otpInput.value = '';
    }

    initForm() {
        this.form = this.formBuilder.group({
                twoFactorCode: ['',
                    [
                        Validators.required,
                        Validators.minLength(6),
                        Validators.maxLength(6),
                        Validators.pattern(/^\d{6}$/)
                    ]
                ],
            }
        );
    }

    blockNonNumeric(event: KeyboardEvent) {
        const allowedKeys = [
            'Backspace', 'ArrowLeft', 'ArrowRight', 'Tab', 'Delete'
        ];
        if (
            allowedKeys.includes(event.key) ||
            /^[0-9]$/.test(event.key)
        ) {
            return;
        }
        event.preventDefault();
    }

    submit() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        const cb = () => {
            this.resetVerifyForm();
            this.translate.get(['Success', 'SetUpAuthenticatorSuccess']).subscribe(translations => {
                this.toastService.push('success', translations['Success'], translations['SetUpAuthenticatorSuccess'], 'bottom-center');
            })
            this.dialogVisibility.verifyCode = false;
            this.twoFactorInfo.lastAuthenticatorUpdate = new Date();
            this.setData()
        }
        this.factorVerificationService.enableAuthenticator(this.form.value, cb);
    }

    removeAuthenticator() {
        const cb = () => {
            this.resetVerifyForm();
            this.translate.get(['Success', 'RemoveUpAuthenticatorSuccess']).subscribe(translations => {
                this.toastService.push('success', translations['Success'], translations['RemoveUpAuthenticatorSuccess'], 'bottom-center');
            })
            this.dialogVisibility.disableAuthenticator = false;
            this.twoFactorInfo.lastAuthenticatorUpdate = null;
            this.setData();
        }
        this.factorVerificationService.disableAuthenticator(cb);
    }

    get id() {
        return this.sessionService.id;
    }

    private loadData() {
        this.factorVerificationService.twoFactorInfo().subscribe({
            next: (response) => {
                this.twoFactorInfo = response;
                this.setData();
            }
        });
    }

    private setData() {
        const info = this.twoFactorInfo;
        const formattedDate = info.lastAuthenticatorUpdate
            ? new Date(info.lastAuthenticatorUpdate).toLocaleDateString(this.languageService.getCurrentLanguage(), {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
            })
            : null;
        this.translate.get(['LastChanged'], { value: formattedDate }).subscribe(translations => {
            const authenticatorDescription = info.lastAuthenticatorUpdate ? translations['LastChanged'] : 'AuthenticatorText1';
            const authenticatorDescriptionIcon = info.lastAuthenticatorUpdate ? 'pi-check-circle green' : 'pi-exclamation-circle yellow';
            this.items = [
                {
                    title: 'Authenticator',
                    description: authenticatorDescription,
                    descriptionIcon: authenticatorDescriptionIcon,
                    titleIcon: 'pi-lock'
                },
                {
                    title: 'RecoveryEmail',
                    description: info.email,
                    descriptionIcon: 'pi-check-circle green',
                    titleIcon: 'pi-envelope'
                }

            ];
            this.buttonText = info.twoFactorEnable ? "Turn2FAOff" : "Turn2FAOn";
        })

    }

    private handleTranslateMessage() {
        const playStoreLink = '<a href="https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2" target="_blank">';
        const appStoreLink = '<a href="https://apps.apple.com/us/app/google-authenticator/id388497605" target="_blank">';

        this.translate.get('AuthenticatorFirstStep').subscribe((res: string) => {
            this.translatedMessage = res
                .replace('<a1>', playStoreLink)
                .replace('</a1>', '</a>')
                .replace('<a2>', appStoreLink)
                .replace('</a2>', '</a>');
        });

    }

    protected readonly Action = Action;
}
