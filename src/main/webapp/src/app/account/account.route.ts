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
		]
	},
]
