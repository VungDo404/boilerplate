import { Component, Input } from '@angular/core';
import { NgClass, NgIf } from "@angular/common";
import { AccordionItem } from "../accordion.component";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'app-accordion-item',
    imports: [
        NgClass,
        RouterLink,
        NgIf
    ],
    templateUrl: './accordion-item.component.html',
    standalone: true,
    styleUrl: './accordion-item.component.scss'
})
export class AccordionItemComponent {
    @Input() mode: AccordionMode = 'wide';
    @Input() isLast!: boolean;
    @Input() item!: AccordionItem;
}
