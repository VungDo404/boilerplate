import {Routes} from "@angular/router";
import { Action } from "../../shared/const/app.enum";
import { PermissionGuard } from "../../shared/guard/permission.guard";

export const ADMIN_ROUTE: Routes = [
	{
		path: "",
 		loadComponent: () => import("./admin.component").then(m => m.AdminComponent),
		canActivate: [PermissionGuard],
		data: { mask: Action.Admin, type: "Application" } as BaseAuthority,
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
