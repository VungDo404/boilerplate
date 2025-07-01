import { Component, OnInit } from '@angular/core';
import { LogoutService } from "./logout.service";
import { NgxSpinnerService } from "ngx-spinner";

@Component({
    selector: 'app-logout',
    imports: [],
    templateUrl: './logout.component.html',
    standalone: true,
    styleUrl: './logout.component.scss'
})
export class LogoutComponent implements OnInit {
    constructor(private logoutService: LogoutService, private spinnerService: NgxSpinnerService) {}

    ngOnInit(): void {
        this.spinnerService.show(undefined, {
            size: 'medium',
            bdColor: 'rgba(0, 0, 0, 1)'
        });
        const cb = () => {
            this.spinnerService.hide();
            window.location.href = '/';

        }
        this.logoutService.logout(cb);
    }

}
