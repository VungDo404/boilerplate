import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from "@angular/router";
import { TranslatePipe } from "@ngx-translate/core";
import { Divider } from "primeng/divider";
import { NgStyle } from "@angular/common";
import { takeUntil } from "rxjs";
import { BaseComponent } from "../../../shared/component/base.component";


@Component({
  selector: 'app-signin-option',
    imports: [
        RouterOutlet,
        RouterLink,
        TranslatePipe,
        Divider,
        NgStyle
    ],
  templateUrl: './signin-option.component.html',
  standalone: true,
  styleUrl: './signin-option.component.scss'
})
export class SigninOptionComponent extends BaseComponent implements OnInit{
    headerTitle!: string;
    constructor(private router: Router) {
        super();
    }

    private update(){
        const url = this.router.url;
        if(url.includes('/signin-option/two-factor-verification')){
            this.headerTitle = '2-StepVerification';
        }else if(url.includes('/signin-option/password')){
            this.headerTitle = 'Password';
        }
    }

    ngOnInit(): void {
        this.update();

        this.router.events
            .pipe(takeUntil(this.destroy$))
            .subscribe(event => {
                if (event instanceof NavigationEnd) {
                    this.update();
                }
            });
    }

}
