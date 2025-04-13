import { Routes } from "@angular/router";

export const MAIN_ROUTE: Routes = [
    {
        path: "",
        loadComponent: () => import("./main.component").then(m => m.MainComponent),
        children: [
            {
                path: "access-denied",
                loadComponent: () => import("./access-denied/access-denied.component").then(m => m.AccessDeniedComponent)
            }
        ]
    }
]