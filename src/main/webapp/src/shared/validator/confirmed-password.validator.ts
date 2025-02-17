import { FormGroup } from "@angular/forms";

export const passwordMatchValidator = (form: FormGroup) => {
    const password = form.get('password');
    const confirmedPassword = form.get('confirmedPassword');

    if (password && confirmedPassword && password.value !== confirmedPassword.value) {
        confirmedPassword.setErrors({ passwordMismatch: true });
    }
}