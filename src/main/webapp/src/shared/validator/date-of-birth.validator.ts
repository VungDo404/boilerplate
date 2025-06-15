import { AbstractControl, FormGroup } from "@angular/forms";

export const dateOfBirthValidator = (control: AbstractControl) => {
    const inputDate = control.value;
    if (!inputDate) return null;

    const dob = new Date(inputDate);
    const today = new Date();

    if (isNaN(inputDate.getTime())) {
        return { invalidDate: true };
    }

    if (dob > today) {
        return { futureDate: true };
    }

    return null;
}