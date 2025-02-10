import {ComponentFactoryResolver, Directive, Input, OnChanges, SimpleChanges, ViewContainerRef} from '@angular/core';
import {NgxSpinnerComponent, NgxSpinnerService} from "ngx-spinner";

@Directive({
	standalone: true,
	selector: '[appBusy]'
})
export class BusyDirective implements OnChanges {
	@Input() busyIf!: boolean;
	private spinnerName = '';
	private isBusy = false;
	constructor(private viewContainerRef: ViewContainerRef, private ngxSpinnerService: NgxSpinnerService) {
		this.loadComponent();
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes.busyIf){
			this.isBusy = changes.busyIf.currentValue;
			this.refreshState();
		}
	}

	loadComponent() {
		const componentRef = this.viewContainerRef.createComponent(NgxSpinnerComponent);
		this.spinnerName = 'busySpinner-' + Date.now(); // generate random name
		let component = <NgxSpinnerComponent>componentRef.instance;
		component.name = this.spinnerName;
		component.fullScreen = false;

		component.type = 'ball-clip-rotate';
		component.size = 'medium';
		component.color = '#5ba7ea';
	}

	refreshState(): void {
		if (this.isBusy === undefined || this.spinnerName === '') {
			return;
		}

		setTimeout(() => {
			if (this.isBusy) {
				this.ngxSpinnerService.show(this.spinnerName);
			} else {
				this.ngxSpinnerService.hide(this.spinnerName);
			}
		}, 1000);
	}

}
