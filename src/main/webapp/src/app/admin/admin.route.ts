import {Routes} from "@angular/router";

export const ADMIN_ROUTE: Routes = [
	{
		path: "",
 		loadComponent: () => import("./admin.component").then(m => m.AdminComponent),
		children: [
			{
				path: "",
				redirectTo: "user",
				pathMatch: "full"
			},
			{
				path: "user",
				loadComponent: () => import("./user/user.component").then(m => m.UserComponent)
			}
		]
	}
]
