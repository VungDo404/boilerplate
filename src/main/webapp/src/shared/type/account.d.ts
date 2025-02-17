declare interface LoginForm {
    username: string;
    password: string;
}

declare interface RegisterForm {
    username: string;
    password: string;
    email: string;
    displayName: string;
}

declare interface RegisterFormWithConfirmedPassword extends RegisterForm {
    confirmedPassword: string;
}

declare type AuthenticationResult = AuthenticationTokenResult | RequireEmailVerificationResult;

declare interface AuthenticationTokenResult {
    accessToken: string;
    encryptedAccessToken: string;
    expiresInSeconds: number;
    shouldChangePasswordOnNextLogin: boolean;
    passwordResetCode?: string;
    isTwoFactorEnabled: boolean;
    requiresTwoFactorVerification: boolean;
}

declare interface RequireEmailVerificationResult {
    email: string;
    requiresEmailVerification: boolean;
}

declare interface RegisterResult {
    canLogin: boolean;
}
