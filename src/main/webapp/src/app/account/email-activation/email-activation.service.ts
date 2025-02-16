import { Injectable } from '@angular/core';
import { ConfigService } from "../../../shared/service/config.service";
import { HttpClient, HttpParams } from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})
export class EmailActivationService {
    private baseUrl!: string;

    constructor(private configService: ConfigService, private http: HttpClient) {
        this.baseUrl = this.configService.baseUrl;
    }

    verify(key: string, cbSuccess: () => void, cbError: () => void) {
        const params = new HttpParams().set('key', key);
        this.http.get(this.baseUrl + "account/email-activation", {params}).subscribe({
                next: () => cbSuccess(),
                error: () => cbError()
            }
        );
    }

    sendEmailVerification(email: string) {

    }
}
