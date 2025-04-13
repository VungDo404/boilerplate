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

@Injectable({
    providedIn: 'root'
})
export class PermissionGuard implements CanActivate {
    constructor(
        private aclService: AclService,
        private router: Router
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
            return this.router.parseUrl("/main/access-denied");

        }
        return true;
    }

}
