import { Routes } from "@angular/router";
import { Action } from "../../../shared/const/app.enum";
import { CURRENT_USER_ID } from "../../../shared/const/app.const";
import { PermissionGuard } from "../../../shared/guard/permission.guard";
import { ProviderGuard } from "../../../shared/guard/provider.guard";

export const SETTING_ROUTE: Routes = [
    {
        path: "",
        loadComponent: () => import("./setting.component").then(m => m.SettingComponent),
        children: [
            {
                path: "account",
                loadComponent: () => import("./account/account.component").then(m => m.AccountComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Write, type: "User", id: CURRENT_USER_ID } as BaseAuthority
            },
            {
                path: "security",
                loadComponent: () => import("./security/security.component").then(m => m.SecurityComponent),
                canActivate: [ProviderGuard, PermissionGuard],
                data: { mask: Action.Write, type: "User", id: CURRENT_USER_ID, allowProvider: ['LOCAL'] } as BaseAuthority
            }
        ]
    }
]