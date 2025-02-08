import { Component, Input } from '@angular/core';
import { AsyncPipe, NgForOf, NgIf } from "@angular/common";
import { AbstractControl } from "@angular/forms";
import { TranslateService } from "@ngx-translate/core";
import { filter, find, concat } from 'lodash';

export type Error = {
  error: string;
  localizationKey: string;
  errorProperty?: string;
}
@Component({
  selector: 'validation-message',
  imports: [
    NgIf,
    NgForOf,
    AsyncPipe
  ],
  templateUrl: './validation-message.component.html',
  standalone: true,
  styleUrl: "validation-message.component.scss"
})
export class ValidationMessageComponent {
  error: Error[] = [];

  @Input() formCtrl!: AbstractControl;
  @Input() set Errors(value: Error[]) {
    this.error = value;
  }

  readonly standardErrors: Error[] = [
    { error: 'required', localizationKey: 'ThisFieldIsRequired' },
    {
      error: 'minlength',
      localizationKey: 'PleaseEnterAtLeastNCharacter',
      errorProperty: 'requiredLength',
    },
    {
      error: 'maxlength',
      localizationKey: 'PleaseEnterNoMoreThanNCharacter',
      errorProperty: 'requiredLength',
    },
    { error: 'email', localizationKey: 'InvalidEmailAddress' },
    { error: 'pattern', localizationKey: 'InvalidPattern', errorProperty: 'requiredPattern' }
  ];

  constructor(private translateService: TranslateService) {}

  get errorDefsInternal(): Error[] {
    const standards = filter(
        this.standardErrors,
        (ed) => !find(this.error, (edC) => edC.error === ed.error)
    );
    return concat(standards, this.error);
  }

  getErrorDefinitionIsInValid(errorDef: Error): boolean {
    return Boolean(this.formCtrl.errors?.[errorDef.error]);
  }

  get firstErrorMessage() {
    const firstError = this.errorDefsInternal.find(errorDef => this.getErrorDefinitionIsInValid(errorDef));
    return firstError ? this.getErrorDefinitionMessage(firstError) : null;
  }

  getErrorDefinitionMessage(errorDef: Error) {
    const errorRequirement = this.formCtrl.errors?.[errorDef.error]?.[errorDef.errorProperty || ''];
    return this.translateService.get(errorDef.localizationKey, { value: errorRequirement });
  }
}
