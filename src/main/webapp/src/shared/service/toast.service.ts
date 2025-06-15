import { Injectable } from '@angular/core';
import { BehaviorSubject } from "rxjs";
import { ToastMessageOptions } from "primeng/api";
import { ToastPositionType } from "primeng/toast";

@Injectable({
    providedIn: 'root'
})
export class ToastService {
    readonly toastSource = new BehaviorSubject<ToastMessageOptions[]>([]);
    readonly toastMessage$ = this.toastSource.asObservable();
    positionSubject= new BehaviorSubject<ToastPositionType>('top-right') ;

    constructor() { }

    push(severity: string, summary: string, detail: string, postion?: ToastPositionType) {
        this.positionSubject.next(postion ?? 'top-right')
        const messageOption = [{
            severity: severity,
            summary: summary,
            detail: detail,
            life: 5000,
            closable: true
        }]
        this.toastSource.next(messageOption);
    }
}
