import { Routes } from '@angular/router';

export const  ACCOUNT_ROUTE: Routes = [
	{
		path: "",
		loadComponent: () => import("./account.component").then(m => m.AccountComponent),
		children: [
			{
				path: "",
				redirectTo: "login",
				pathMatch: "full"
			},
			{
				path: "login",
				loadComponent: () => import("./login/login.component").then(m => m.LoginComponent)
			},
			{
				path: "register",
				loadComponent: () => import("./register/register.component").then(m => m.RegisterComponent)
			},
			{
				path: "forgot-password",
				loadComponent: () => import("./forgot-password/forgot-password.component").then(m => m.ForgotPasswordComponent)
			},
			{
				path: "reset-password",
				loadComponent: () => import("./reset-password/reset-password.component").then(m => m.ResetPasswordComponent)
			},
			{
				path: "email-activation",
				loadComponent: () => import("./email-activation/email-activation.component").then(m => m.EmailActivationComponent)
			},
			{
				path: "send-email",
				loadComponent: () => import("./send-email/send-email.component").then(m => m.SendEmailComponent)
			},
			{
				path: "send-code",
				loadComponent: () => import("./send-two-factor-code/send-two-factor-code.component").then(m => m.SendTwoFactorCodeComponent)

			},
			{
				path: "validate-code",
				loadComponent: () => import("./validate-two-factor-code/validate-two-factor-code.component").then(m => m.ValidateTwoFactorCodeComponent)
			},
			{
				path: "oauth-callback",
				loadComponent: () => import("./oauth2-callback/oauth2-callback.component").then(m => m.Oauth2CallbackComponent)
			}

		]
	},
]
