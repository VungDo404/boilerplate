import { Injectable } from '@angular/core';
import { environment } from "../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class ConfigService {
    private readonly config;

    constructor() {
        this.config = environment;
    }

    get baseUrl(){
        return this.config.apiUrl;
    }
}
