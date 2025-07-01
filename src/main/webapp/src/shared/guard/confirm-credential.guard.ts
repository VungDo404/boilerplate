import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    GuardResult,
    MaybeAsync,
    Router,
    RouterStateSnapshot, UrlTree
} from '@angular/router';
import { ConfirmCredentialService } from "../../app/account/confirm-credential/confirm-credential.service";
import { Observable, map } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class ConfirmCredentialGuard implements CanActivate {
    constructor(private confirmCredentialService: ConfirmCredentialService, private router: Router) {}

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Observable<boolean | UrlTree> {
        return this.confirmCredentialService.isConfirmCredential().pipe(
            map((isConfirmed) => {
                if (isConfirmed) return true;
                const urlTree = this.router.parseUrl('/confirm-credential');
                urlTree.queryParams = { redirect: state.url };
                return urlTree;
            })
        );
    }

}
