import { Component, Input, TemplateRef } from '@angular/core';
import { NgForOf, NgIf } from "@angular/common";
import { AccordionItemComponent } from "./accordion-item/accordion-item.component";


export interface AccordionItem{
    title: string;
    titleIcon: string;
    description: string;
    descriptionIcon?: string;
    content?: string | TemplateRef<any>;
    routerLink?: string;
}

@Component({
    selector: 'app-accordion',
    imports: [
        NgIf,
        NgForOf,
        AccordionItemComponent
    ],
    templateUrl: './accordion.component.html',
    standalone: true,
    styleUrl: './accordion.component.scss'
})
export class AccordionComponent {
    @Input() mode: AccordionMode = 'compact';
    @Input() items!: AccordionItem[];
    @Input() title!: string | undefined;
    @Input() description!: string | undefined;
}
