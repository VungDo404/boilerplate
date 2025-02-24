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

declare type AuthenticationResult = AuthenticationTokenResult
    | RequireEmailVerificationResult
    | ShouldChangePasswordOnNextLoginResult
    | RequireTwoFactorAuthenticationResult;

declare interface AuthenticationTokenResult {
    accessToken: string;
    encryptedAccessToken: string;
    expiresInSeconds: number;
}

declare interface RequireTwoFactorAuthenticationResult {
    isTwoFactorEnabled: boolean;
    userId: string;
    twoFactorProviders: TwoFactorProvider[]
}

declare type TwoFactorProvider = "EMAIL" | "SMS" | "GOOGLE_AUTHENTICATOR";

declare interface SendTwoFactorCode {
    userId: string;
    provider: TwoFactorProvider;
}

declare type SendTwoFactorResult = EmailProviderResult | GoogleAuthenticatorProviderResult;

declare interface EmailProviderResult {
    provider: "EMAIL"
}

declare interface GoogleAuthenticatorProviderResult {
    provider: "GOOGLE_AUTHENTICATOR",
    uri: string;
    secret: string;
}

declare interface ShouldChangePasswordOnNextLoginResult {
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
