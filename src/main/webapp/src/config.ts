import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';

import {provideAnimationsAsync} from "@angular/platform-browser/animations/async";
import {providePrimeNG} from "primeng/config";
import Aura from '@primeng/themes/aura';
import {provideRouter} from "@angular/router";
import {ROUTE} from "./root.route";

export const config: ApplicationConfig = {
	providers: [
		provideRouter(ROUTE),
		provideZoneChangeDetection({eventCoalescing: true}),
		provideAnimationsAsync(),
		providePrimeNG({
			theme: {
				preset: Aura,
				options: {
					darkModeSelector: '.my-app-dark'
				}
			}
		})
	]
};
