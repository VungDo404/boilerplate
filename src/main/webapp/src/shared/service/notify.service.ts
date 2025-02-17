import { Injectable } from '@angular/core';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Injectable({
    providedIn: 'root'
})
export class NotifyService {

    info(message: string, title: string = 'Info', options: any, cb: (result: SweetAlertResult<any>) => void): void {
        Swal.fire({
            title,
            text: message,
            icon: 'info',
            ...options,
        }).then(result => {
            cb(result);
        });
    }

    success(message: string, title: string = 'Success', options: any, cb: (result: SweetAlertResult<any>) => void): void {
        Swal.fire({
            title,
            text: message,
            icon: 'success',
            ...options,
        }).then(result => {
            cb(result);
        });
    }

    warn(message: string, title: string = 'Warning', options?: any): void {
        Swal.fire({
            title,
            text: message,
            icon: 'warning',
            ...options,
        });
    }

    error(message: string, title: string = 'Error', options?: any): void {
        Swal.fire({
            title,
            text: message,
            icon: 'error',
            ...options,
        });
    }
}
