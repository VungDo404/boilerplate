import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class NotifyService {

  info(message: string, title: string = 'Info', options?: any): void {
    Swal.fire({
      title,
      text: message,
      icon: 'info',
      ...options,
    });
  }

  success(message: string, title: string = 'Success', options?: any): void {
    Swal.fire({
      title,
      text: message,
      icon: 'success',
      ...options,
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
