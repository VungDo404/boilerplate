import { Routes } from "@angular/router";

export const SETTING_ROUTE: Routes = [
    {
        path: "",
        loadComponent: () => import("./setting.component").then(m => m.SettingComponent),
        children: [
            {
                path: "account",
                loadComponent: () => import("./account/account.component").then(m => m.AccountComponent)
            }
        ]
    }
]