import { Routes } from "@angular/router";
import { ConfirmCredentialGuard } from "../../../shared/guard/confirm-credential.guard";

export const SIGN_IN_OPTION_ROUTE: Routes = [
    {
        path: "signin-option",
        loadComponent: () => import("./signin-option.component").then(m => m.SigninOptionComponent),
        children: [
            {
                path: "password",
                loadComponent: () => import("./password/password.component").then(m => m.PasswordComponent),
                canActivate: [ConfirmCredentialGuard]
            },
            {
                path: "two-factor-verification",
                loadComponent: () => import("./two-factor-verification/two-factor-verification.component").then(m => m.TwoFactorVerificationComponent),
                canActivate: [ConfirmCredentialGuard]
            },

        ]
    }
]