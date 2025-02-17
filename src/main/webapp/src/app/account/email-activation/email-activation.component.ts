import { Component, OnDestroy, OnInit } from '@angular/core';
import { Image } from "primeng/image";
import { ActivatedRoute, Router } from "@angular/router";
import { Button } from "primeng/button";
import { TranslateService } from "@ngx-translate/core";
import { EmailActivationService } from "./email-activation.service";
import { SkeletonDirective } from "../../../shared/directive/skeleton.directive";
import { Subject, takeUntil } from "rxjs";

@Component({
    selector: 'app-email-activation',
    imports: [
        Image,
        Button,
        SkeletonDirective
    ],
    templateUrl: './email-activation.component.html',
    standalone: true,
    styleUrl: './email-activation.component.scss'
})
export class EmailActivationComponent implements OnInit, OnDestroy {
    imageSrc = "";
    buttonText = "";
    messageText = "";
    isLoading = true;
    private destroy$ = new Subject<void>();

    constructor(
        private route: ActivatedRoute,
        private emailActivationService: EmailActivationService,
        private translate: TranslateService,
        private router: Router
    ) {}

    ngOnInit(): void {
        const cbSuccess = () => {
            this.imageSrc = "src/assets/images/03.png";
            this.translate.get(['GoToLogin', 'VerifySuccessMessage'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.buttonText = translations['GoToLogin'];
                    this.messageText = translations['VerifySuccessMessage'];
                });
            this.isLoading = false;

        }
        const cbError = () => {
            this.imageSrc = "src/assets/images/04.png";
            this.translate.get(['ResendVerification', 'VerifyFailedMessage'])
                .pipe(takeUntil(this.destroy$))
                .subscribe(translations => {
                    this.buttonText = translations['ResendVerification'];
                    this.messageText = translations['VerifyFailedMessage'];
                });
            this.isLoading = false;
        }
        this.route.queryParamMap.subscribe(params => {
            const token = params.get('key') || '';
            this.emailActivationService.verify(token, cbSuccess, cbError);
        });
    }

    navigateLogin() {
        this.router.navigate(['/account/login']);
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }


}
