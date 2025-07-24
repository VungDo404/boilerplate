import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { ConfigService } from "../config.service";
import { fetchEventSource } from "@microsoft/fetch-event-source";
import { LocalStorageService } from "../local-storage.service";
import { EventSourceMessage } from "@microsoft/fetch-event-source/lib/cjs/parse";

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private readonly baseUrl!: string;

    constructor(private http: HttpClient, private configService: ConfigService, private localStorageService: LocalStorageService) {
        this.baseUrl = this.configService.baseUrl + "notification";
    }

    getNotification() {
        const url = this.baseUrl;
        return this.http.get<Notification[]>(url);
    }

     notificationSource(onmessage: (event: EventSourceMessage) => void) {
        const url = this.baseUrl + '/subscribe';
        const token = this.localStorageService.getAccessToken()?.accessToken;
        const headers: Record<string, string> = {};
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        fetchEventSource(url, {
            headers,
            onmessage
        });
    }
}
