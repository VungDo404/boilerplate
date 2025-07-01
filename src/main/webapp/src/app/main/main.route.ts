import { Routes } from "@angular/router";

export const MAIN_ROUTE: Routes = [
    {
        path: "",
        loadComponent: () => import("./main.component").then(m => m.MainComponent),
        children: [
            {
                path: "",
                loadComponent: () => import("./home/home.component").then(m => m.HomeComponent),
            },
            {
                path: "",
                loadChildren: () => import("./setting/setting.route").then(m => m.SETTING_ROUTE)
            },
            {
                path: "",
                loadChildren: () => import("./signin-option/signin-option.route").then(m => m.SIGN_IN_OPTION_ROUTE)
            },
            {
                path: "access-denied",
                loadComponent: () => import("./access-denied/access-denied.component").then(m => m.AccessDeniedComponent)
            }

        ]
    }
]