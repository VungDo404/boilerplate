import { Injectable } from '@angular/core';
import { LoginService } from "../login/login.service";
import { ConfigService } from "../../../shared/service/config.service";

@Injectable({
    providedIn: 'root'
})
export class ValidateTwoFactorCodeService {
    private baseUrl!: string;

    constructor(
        public loginService: LoginService,
        private configService: ConfigService,
    ) {
        this.baseUrl = this.configService.baseUrl;
    }

    validate(code: string, cb: () => void) {
        this.loginService.loginForm .get('twoFactorCode')?.setValue(code);
        this.loginService.authenticate(cb)
    }
}
