import { Injectable } from '@angular/core';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Injectable({
    providedIn: 'root'
})
export class NotifyService {

    private o1 = {
        showConfirmButton: true,
        confirmButtonText: 'OK',
        allowOutsideClick: false,
        confirmButtonColor: '#EA6365',
        timer: 4000,
        timerProgressBar: true,
    }

    option1(confirmButtonText?: string){
        return {
            ... this.o1,
            ...(confirmButtonText ? { confirmButtonText } : {})
        }
    }

    info(message: string, title: string = 'Info', options: any, cb: () => void): void {
        Swal.fire({
            title,
            text: message,
            icon: 'info',
            ...options,
        }).then(result => {
            if (result.isConfirmed || result.dismiss === Swal.DismissReason.timer){
                cb();
            }

        });
    }

    success(message: string, title: string = 'Success', options: any, cb: () => void): void {
        Swal.fire({
            title,
            text: message,
            icon: 'success',
            ...options,
        }).then(result => {
            if (result.isConfirmed || result.dismiss === Swal.DismissReason.timer){
                cb();
            }
        });
    }

    warn(message: string, title: string = 'Warning', options: any, cb: () => void): void {
        Swal.fire({
            title,
            text: message,
            icon: 'warning',
            ...options,
        }).then(result => {
            if (result.isConfirmed || result.dismiss === Swal.DismissReason.timer){
                cb();
            }
        });
    }

    error(message: string, title: string = 'Error', options: any, cb: () => void): void {
        Swal.fire({
            title,
            text: message,
            icon: 'error',
            ...options,
        }).then(result => {
            if (result.isConfirmed || result.dismiss === Swal.DismissReason.timer){
                cb();
            }
        });
    }
}
