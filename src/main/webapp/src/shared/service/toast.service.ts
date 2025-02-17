import { Injectable } from '@angular/core';
import { BehaviorSubject } from "rxjs";
import { ToastMessageOptions } from "primeng/api";

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  readonly toastSource = new BehaviorSubject<ToastMessageOptions[]>([]);
  readonly toastMessage$ = this.toastSource.asObservable();
  constructor() { }
}
