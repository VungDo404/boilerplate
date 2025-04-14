import { Component } from '@angular/core';
import { RouterOutlet } from "@angular/router";
import { NgxSpinnerComponent } from "ngx-spinner";
import { Toast } from "primeng/toast";
import { merge, Subscription } from "rxjs";
import { MessageService, ToastMessageOptions } from "primeng/api";
import { ToastService } from "../shared/service/toast.service";

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, NgxSpinnerComponent, Toast],
    providers: [MessageService],
    templateUrl: './app.component.html',
    standalone: true,
    styleUrl: './app.component.scss'
})
export class AppComponent {
    toastMessageOptions: ToastMessageOptions[] = [];
    private toastSubscription!: Subscription;

    constructor(private toastService: ToastService, private messageService: MessageService) {
        this.toastSubscription = merge(
            this.toastService.toastMessage$,
        ).subscribe((option: ToastMessageOptions[]) => {
            this.toastMessageOptions = option;
            this.messageService.addAll(this.toastMessageOptions);
        });
    }
}
