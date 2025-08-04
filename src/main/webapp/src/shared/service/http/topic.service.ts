import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { ConfigService } from "../config.service";

@Injectable({
    providedIn: 'root'
})
export class TopicService {
    private readonly baseUrl!: string;

    constructor(private http: HttpClient, private configService: ConfigService) {
        this.baseUrl = this.configService.baseUrl + "topic";
    }

    toggleTopicSubscription(topicId: number, userId: string){
        const url = this.baseUrl + `/${topicId}/user/${userId}`;
        return this.http.patch(url, {});
    }
}
