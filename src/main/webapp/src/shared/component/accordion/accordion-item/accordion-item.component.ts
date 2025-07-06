import {
    Component, EventEmitter,
    Input, Output,
} from '@angular/core';
import { NgClass, NgIf } from "@angular/common";
import { AccordionItem } from "../accordion.component";
import { RouterLink } from "@angular/router";
import { TranslatePipe } from "@ngx-translate/core";

@Component({
    selector: 'app-accordion-item',
    imports: [
        NgClass,
        RouterLink,
        NgIf,
        TranslatePipe
    ],
    templateUrl: './accordion-item.component.html',
    standalone: true,
    styleUrl: './accordion-item.component.scss'
})
export class AccordionItemComponent {
    @Input() mode: AccordionMode = 'wide';
    @Input() isLast!: boolean;
    @Input() item!: AccordionItem;
    @Input() id!: number;
    @Input() isOpen: boolean = false;
    @Output() onToggleItem = new EventEmitter<AccordionItemComponent>();
    constructor() {}

    toggleContent(){
        if(this.isOpen){
            this.isOpen = false;
        }
        this.onToggleItem.emit(this);
    }
}
