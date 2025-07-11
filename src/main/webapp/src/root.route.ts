import { Routes } from '@angular/router';

export const ROUTE: Routes = [
    {
        path: "admin",
        loadChildren: () => import("./app/admin/admin.route").then(m => m.ADMIN_ROUTE)
    },
    {
        path: "",
        loadChildren: () => import("./app/main/main.route").then(m => m.MAIN_ROUTE)
    },
    {
        path: "",
        loadChildren: () => import("./app/account/account.route").then(m => m.ACCOUNT_ROUTE)
    },
    { path: '**', redirectTo: "/" }
]
