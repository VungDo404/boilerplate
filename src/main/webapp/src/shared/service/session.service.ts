import { Injectable } from '@angular/core';
import { AccountService } from "./http/account.service";
import { map, tap } from "rxjs";
import { DEFAULT_UUID } from "../const/app.const";

@Injectable({
    providedIn: 'root'
})
export class SessionService {
    private authorities!: Authority[];
    private userId: string = DEFAULT_UUID;

    constructor(private accountService: AccountService) { }

    profile() {
        return this.accountService.profile().pipe(
            tap((response) => {
                this.authorities = response.authorities;
                this.userId = response.userId ?? this.userId;
            }),
            map(() => void 0)
        );
    }

    get permissions(){
        return this.authorities;
    }

    get isAuthenticated(){
        return this.userId !== DEFAULT_UUID;
    }
}
