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

declare type AuthenticationResult = AuthenticationTokenResult | RequireEmailVerificationResult | ShouldChangePasswordOnNextLoginResult;

declare interface AuthenticationTokenResult {
    accessToken: string;
    encryptedAccessToken: string;
    expiresInSeconds: number;
    isTwoFactorEnabled: boolean;
    requiresTwoFactorVerification: boolean;
}

declare interface ShouldChangePasswordOnNextLoginResult{
    passwordResetCode: string;
    shouldChangePasswordOnNextLogin: boolean;
}

declare interface RequireEmailVerificationResult {
    email: string;
    requiresEmailVerification: boolean;
}

declare interface RegisterResult {
    canLogin: boolean;
}
