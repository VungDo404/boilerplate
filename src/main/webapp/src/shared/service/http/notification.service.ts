import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { ConfigService } from "../config.service";
import { SseClient } from "ngx-sse-client";


@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private readonly baseUrl!: string;

    constructor(private http: HttpClient, private configService: ConfigService, private sseClient: SseClient) {
        this.baseUrl = this.configService.baseUrl + "notification";
    }

    getNotification() {
        const url = this.baseUrl;
        return this.http.get<Notification[]>(url);
    }

     notificationSource() {
        const url = this.baseUrl + '/subscribe';
        return this.sseClient.stream(url, {keepAlive: true, reconnectionDelay: 2000})
    }

    deleteNotification(notificationId: number, userId: string){
        const url = this.baseUrl + `/${notificationId}/user/${userId}`;
        return this.http.delete(url);
    }

    toggleRead(notificationId: number, userId: string){
        const url = this.baseUrl + `/${notificationId}/user/${userId}`;
        return this.http.patch(url, {});
    }
}
