import { Component } from '@angular/core';
import { RouterOutlet } from "@angular/router";
import { NgxSpinnerComponent } from "ngx-spinner";
import { Toast, ToastPositionType } from "primeng/toast";
import { BehaviorSubject, merge, Subscription } from "rxjs";
import { MessageService, ToastMessageOptions } from "primeng/api";
import { ToastService } from "../shared/service/toast.service";
import { AsyncPipe } from "@angular/common";

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, NgxSpinnerComponent, Toast, AsyncPipe],
    providers: [MessageService],
    templateUrl: './app.component.html',
    standalone: true,
    styleUrl: './app.component.scss'
})
export class AppComponent {
    toastMessageOptions: ToastMessageOptions[] = [];
    private toastSubscription!: Subscription;
    position$!: BehaviorSubject<ToastPositionType>;
    constructor(private toastService: ToastService, private messageService: MessageService) {
        this.toastSubscription = merge(
            this.toastService.toastMessage$,
        ).subscribe((option: ToastMessageOptions[]) => {
            this.toastMessageOptions = option;
            this.messageService.addAll(this.toastMessageOptions);
        });
        this.position$ = this.toastService.positionSubject;
    }

}
