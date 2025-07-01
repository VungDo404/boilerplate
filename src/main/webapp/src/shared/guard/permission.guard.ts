import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    GuardResult,
    MaybeAsync,
    Router,
    RouterStateSnapshot
} from '@angular/router';
import { AclService } from "../service/acl.service";
import { SessionService } from "../service/session.service";
import { CURRENT_USER_ID } from "../const/app.const";

@Injectable({
    providedIn: 'root'
})
export class PermissionGuard implements CanActivate {
    constructor(
        private aclService: AclService,
        private router: Router,
        private sessionService: SessionService,
    ) {}

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): MaybeAsync<GuardResult> {

        const { id, type, mask } = route.data as BaseAuthority;
        const temp = id === CURRENT_USER_ID ? this.sessionService.id : id;
        const targetId = route.params['id'] ? route.params['id'] : temp;

        if (type && mask) {

            let isAccess = targetId ?
                this.aclService.hasPermission(targetId, type, mask) :
                this.aclService.hasPermissionOnType(type, mask);
            if (isAccess) return isAccess;
            return this.selectBestRoute(state);

        }
        return true;
    }

    private selectBestRoute(state: RouterStateSnapshot){
        if(this.sessionService.isAuthenticated){
            if(state.url.includes("account")) return this.router.parseUrl("/");
            return this.router.parseUrl("/access-denied");
        }else{
            return this.router.createUrlTree(['/login'], {
                queryParams: { redirectUrl: state.url }
            });
        }
    }

}
