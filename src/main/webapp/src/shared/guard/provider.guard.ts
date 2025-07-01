import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    GuardResult,
    MaybeAsync,
    Router,
    RouterStateSnapshot
} from '@angular/router';
import { SessionService } from "../service/session.service";

@Injectable({
    providedIn: 'root'
})
export class ProviderGuard implements CanActivate {
    constructor(private sessionService: SessionService, private router: Router) {}
    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): MaybeAsync<GuardResult> {
        const { allowProvider } = route.data as BaseAuthority;
        if(allowProvider){
            if(!allowProvider.includes(this.sessionService.provider))
                return this.router.parseUrl("/main/access-denied");
        }
        return true;
    }

}
