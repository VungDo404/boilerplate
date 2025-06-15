import { Routes } from '@angular/router';
import { PermissionGuard } from "../../shared/guard/permission.guard";
import { ROOT_OBJECT } from "../../shared/const/app.const";
import { Action } from "../../shared/const/app.enum";

export const ACCOUNT_ROUTE: Routes = [
    {
        path: "",
        loadComponent: () => import("./account.component").then(m => m.AccountComponent),
        children: [
            {
                path: "login",
                loadComponent: () => import("./login/login.component").then(m => m.LoginComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Read, type: "Authentication", id: ROOT_OBJECT } as BaseAuthority
            },
            {
                path: "register",
                loadComponent: () => import("./register/register.component").then(m => m.RegisterComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Create, type: "Authentication", id: ROOT_OBJECT } as BaseAuthority
            },
            {
                path: "forgot-password",
                loadComponent: () => import("./forgot-password/forgot-password.component").then(m => m.ForgotPasswordComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Write, type: "Authentication", id: ROOT_OBJECT } as BaseAuthority
            },
            {
                path: "reset-password",
                loadComponent: () => import("./reset-password/reset-password.component").then(m => m.ResetPasswordComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Write, type: "Authentication", id: ROOT_OBJECT } as BaseAuthority
            },
            {
                path: "email-activation",
                loadComponent: () => import("./email-activation/email-activation.component").then(m => m.EmailActivationComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Write, type: "Authentication", id: ROOT_OBJECT } as BaseAuthority
            },
            {
                path: "send-email",
                loadComponent: () => import("./send-email/send-email.component").then(m => m.SendEmailComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Write, type: "Authentication", id: ROOT_OBJECT } as BaseAuthority
            },
            {
                path: "send-code",
                loadComponent: () => import("./send-two-factor-code/send-two-factor-code.component").then(m => m.SendTwoFactorCodeComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Read, type: "Authentication", id: ROOT_OBJECT } as BaseAuthority

            },
            {
                path: "validate-code",
                loadComponent: () => import("./validate-two-factor-code/validate-two-factor-code.component").then(m => m.ValidateTwoFactorCodeComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Read, type: "Authentication", id: ROOT_OBJECT } as BaseAuthority
            },
            {
                path: "oauth-callback",
                loadComponent: () => import("./oauth2-callback/oauth2-callback.component").then(m => m.Oauth2CallbackComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Read, type: "Authentication", id: ROOT_OBJECT } as BaseAuthority
            },
            {
                path: "logout",
                loadComponent: () => import("./logout/logout.component").then(m => m.LogoutComponent),
                canActivate: [PermissionGuard],
                data: { mask: Action.Delete, type: "Authentication", id: ROOT_OBJECT } as BaseAuthority
            }
        ]
    },
]
