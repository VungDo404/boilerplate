import { Injectable } from '@angular/core';
import { AccountService } from "./http/account.service";
import { map, tap } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class SessionService {
    private authorities!: Authority[];
    private userId: string = "00000000-0000-0000-0000-000000000000";

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
}
