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
import { ROOT_OBJECT } from "../const/app.const";
import { Action } from "../const/app.enum";

@Injectable({
    providedIn: 'root'
})
export class PermissionGuard implements CanActivate {
    constructor(
        private aclService: AclService,
        private router: Router,
        private sessionService: SessionService
    ) {}

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): MaybeAsync<GuardResult> {
        const { id, type, mask } = route.data as BaseAuthority;
        const targetId = route.params['id'] ? route.params['id'] : id;

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
        if(this.aclService.hasPermission(ROOT_OBJECT, "Application", Action.Admin))
            return this.router.parseUrl("/admin");
        if(this.sessionService.isAuthenticated && state.url.includes("account"))
            return this.router.parseUrl("/main");
        return this.router.parseUrl("/main/access-denied");
    }

}
