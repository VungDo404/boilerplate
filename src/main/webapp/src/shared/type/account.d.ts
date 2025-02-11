declare interface LoginForm {
    username: string;
    password: string;
}

declare interface RegisterForm{
    username: string;
    password: string;
    email: string;
    displayName: string;
}

declare interface RegisterFormWithConfirmedPassword extends RegisterForm{
    confirmedPassword: string;
}

