import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class LocalStorageService {
    private readonly ACCESS_TOKEN = "access_token";

    constructor() { }

    setItem(key: string, value: any): void {
        localStorage.setItem(key, JSON.stringify(value));
    }

    setAccessToken(accessToken: string, expiredDate: Date) {
        this.setItem(this.ACCESS_TOKEN, {accessToken, expiredDate});
    }

    removeAccessToken(){
        this.removeItem(this.ACCESS_TOKEN);
    }

    getItem<T>(key: string): T | null {
        const item = localStorage.getItem(key);
        return item ? JSON.parse(item) as T : null;
    }

    getAccessToken(){
        return this.getItem<AccessTokenModel>(this.ACCESS_TOKEN);
    }

    removeItem(key: string): void {
        localStorage.removeItem(key);
    }

    hasItem(key: string): boolean {
        return localStorage.getItem(key) !== null;
    }

    clear(): void {
        localStorage.clear();
    }
}
