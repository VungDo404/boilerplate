import { Injectable } from '@angular/core';
import { AccountService } from "./http/account.service";
import { BehaviorSubject, combineLatest, map, tap } from "rxjs";
import { DEFAULT_UUID, DISPLAY_NAME, USERNAME } from "../const/app.const";

@Injectable({
    providedIn: 'root'
})
export class SessionService {
    private authoritiesSubject = new BehaviorSubject<Authority[]>([]);
    private userIdSubject = new BehaviorSubject<string>(DEFAULT_UUID);
    private usernameSubject = new BehaviorSubject<string>(USERNAME);
    private nameSubject = new BehaviorSubject<string>(DISPLAY_NAME);
    private avatarSubject = new BehaviorSubject<string>('');
    private providerSubject = new BehaviorSubject<Provider>('LOCAL');

    readonly authorities$ = this.authoritiesSubject.asObservable();
    readonly userId$ = this.userIdSubject.asObservable();
    readonly username$ = this.usernameSubject.asObservable();
    readonly name$ = this.nameSubject.asObservable();
    readonly avatar$ = this.avatarSubject.asObservable().pipe(
        map(avatar => avatar || `https://api.dicebear.com/7.x/bottts/svg?seed=${ this.usernameSubject.value }`)
    );

    readonly sessionState$ = combineLatest([
        this.authorities$, this.userId$, this.username$, this.name$, this.avatar$
    ]).pipe(map(([authorities, userId, username, name, avatar]) => ({
        authorities, userId, username, name, avatar
    })));

    constructor(private accountService: AccountService) {
        this.avatarSubject.next(`https://api.dicebear.com/7.x/bottts/svg?seed=${ USERNAME }`);
    }

    profile() {
        return this.accountService.profile().pipe(
            tap((response) => {
                this.authoritiesSubject.next(response.authorities);
                this.userIdSubject.next(response.userId ?? this.userIdSubject.value);
                this.usernameSubject.next(response.username ?? this.usernameSubject.value);
                this.nameSubject.next(response.displayName ?? this.nameSubject.value);
                this.avatarSubject.next(
                    response.avatar ?? `https://api.dicebear.com/7.x/bottts/svg?seed=${ this.usernameSubject.value }`
                );
                this.providerSubject.next(response.provider ?? this.providerSubject.value);
            }),
            map(() => void 0),
        );
    }

    get username(){
        return this.usernameSubject.value;
    }

    get provider(){
        return this.providerSubject.value;
    }

    get permissions() {
        return this.authoritiesSubject.value;
    }

    get id(){
        return this.userIdSubject.value;
    }

    get avatar(){
        return this.avatarSubject.value;
    }

    readonly isAuthenticated$ = this.userId$.pipe(
        map(userId => {
            return userId !== DEFAULT_UUID;
        })
    );

    get isAuthenticated(){
        return this.userIdSubject.value !== DEFAULT_UUID;
    }

    reset(): void {
        this.authoritiesSubject.next([]);
        this.userIdSubject.next(DEFAULT_UUID);
        this.usernameSubject.next(USERNAME);
        this.nameSubject.next(DISPLAY_NAME);
        this.avatarSubject.next(`https://api.dicebear.com/7.x/bottts/svg?seed=${ USERNAME }`);
    }
}
