import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    standalone: true,
    name: 'capitalizeFirstOnly'
})
export class CapitalizeFirstOnlyPipe implements PipeTransform {

    transform(value: string): unknown {
        if (!value) return '';
        const words = value.trim().split(' ');
        if (words.length === 0) return '';
        return [
            words[0].charAt(0).toUpperCase() + words[0].slice(1).toLowerCase(),
            ...words.slice(1).map(w => w.toLowerCase())
        ].join(' ');
    }

}
