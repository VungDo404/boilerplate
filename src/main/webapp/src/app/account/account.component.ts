import { Component } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from "@angular/router";
import { ImageModule } from "primeng/image";
import { Divider } from "primeng/divider";

@Component({
    selector: 'app-account',
    imports: [RouterOutlet, ImageModule, RouterLink, Divider],
    templateUrl: './account.component.html',
    standalone: true,
    styleUrl: './account.component.scss'
})
export class AccountComponent {
    title: string = 'Authenticate';
    message: string = '';
    linkText: string = '';
    linkRoute: string = '';

    constructor(private router: Router) {
        this.router.events.subscribe(() => {
            this.updateAuthPage();
        });
    }

    updateAuthPage() {
        const url = this.router.url;
        if (url.includes('/account/login')) {
            this.title = 'Authenticate';
            this.message = "Don't have an account?";
            this.linkText = 'Sign Up';
            this.linkRoute = '/account/register';
        } else if (url.includes('/account/register')) {
            this.title = 'Create Account';
            this.message = 'Already have an account?';
            this.linkText = 'Sign In';
            this.linkRoute = '/account/login';
        }
    }
}
