import { Injectable } from '@angular/core';
import { AuthenticationService } from "../../../shared/service/http/authentication.service";
import { LocalStorageService } from "../../../shared/service/local-storage.service";
import { finalize } from "rxjs";
import { SessionService } from "../../../shared/service/session.service";

@Injectable({
    providedIn: 'root'
})
export class LogoutService {

    constructor(
        private authenticationService: AuthenticationService,
        private localStorageService: LocalStorageService,
        private sessionService: SessionService
    ) {}

    logout(cb: () => void) {
        this.authenticationService.logout().pipe(finalize(cb)).subscribe({
                next: () => {
                    this.localStorageService.removeAccessToken();
                    this.sessionService.reset();
                }
            }
        )
    }

}
