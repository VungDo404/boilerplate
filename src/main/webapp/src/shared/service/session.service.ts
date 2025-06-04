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
    username = "anonymousUser";
    name = "Anonymous User";
    avatar = "";
    constructor(private accountService: AccountService) { }

    profile() {
        return this.accountService.profile().pipe(
            tap((response) => {
                this.authorities = response.authorities;
                this.userId = response.userId ?? this.userId;
                this.username = response.username ?? this.username;
                this.avatar = response.avatar ?? "https://api.dicebear.com/7.x/bottts/svg?seed=" + this.username;
                this.name = response.displayName ?? this.name;
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
