import { Component } from '@angular/core';
import { Button } from "primeng/button";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

@Component({
	selector: 'app-register',
	imports: [
		Button,
		FormsModule,
		ReactiveFormsModule
	],
	templateUrl: './register.component.html',
	standalone: true,
	styleUrl: './register.component.scss'
})
export class RegisterComponent {
	register(){

	}
}
