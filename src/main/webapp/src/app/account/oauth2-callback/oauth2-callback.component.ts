import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from "@angular/router";
import { LoginService } from "../login/login.service";
import { NgxSpinnerService } from "ngx-spinner";

@Component({
    selector: 'app-oauth2-callback',
    imports: [],
    templateUrl: './oauth2-callback.component.html',
    standalone: true,
    styleUrl: './oauth2-callback.component.scss'
})
export class Oauth2CallbackComponent implements OnInit {
    constructor(private route: ActivatedRoute, private loginService: LoginService, private spinnerService: NgxSpinnerService) {}

    ngOnInit(): void {
        this.spinnerService.show(undefined, {
            size: 'medium',
            bdColor: 'rgba(0, 0, 0, 1)'
        });
        this.route.queryParamMap.subscribe(params => {
            const tokenResult: AuthenticationTokenResult = {
                accessToken: params.get('token') || '',
                expiresInSeconds: +(params.get('expired') ?? '0'),
            }
            const cb = () => {
                this.spinnerService.hide();
                window.location.href = this.loginService.redirectUrl ?? '/';
            }
            this.loginService.login(tokenResult, cb);
        });
    }
}
