declare interface LoginForm {
    username: string;
    password: string;
    twoFactorCode: string;
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

declare interface AccessTokenModel {
    accessToken: string;
    expiredDate: Date;
}

declare type ResourceType =
    | "Application"
    | "Authentication"
    | "Authorization"
    | "User"
    | "File"
    | "Domain";


declare type Mask =
    | 1
    | 2
    | 4
    | 8
    | 16
    | 32;

declare interface BaseAuthority {
    mask: Mask;
    type: ResourceType;
    id: string;
    allowProvider?: Provider[];
}

declare interface Authority extends BaseAuthority {
    granting: boolean;
}

declare type Provider = 'LOCAL' | 'GOOGLE' | 'GITHUB';

declare interface ProfileResult {
    userId?: string;
    authorities: Authority[];
    username?: string;
    avatar?: string;
    displayName?: string;
    provider?: Provider;
}

declare interface ObjectHierarchy {
    type: ResourceType;
    id: string | number;
    parent: ObjectHierarchy | null;
}

declare interface UpdateUserInfo {
    displayName?: string;
    gender?: number;
    dateOfBirth?: Date;
    phoneNumber?: string;
}

declare interface SecurityInfo{
    passwordLastUpdate?: Date;
    twoFactorEnable: boolean;
}

declare interface IsConfirmSecurity{
    confirmed: boolean;
}

declare interface ConfirmSecurity{
    username: string;
    password: string;
}

declare interface ChangePassword{
    id: string;
    newPassword: string;
}

declare interface TwoFactorInfo{
    lastAuthenticatorUpdate: Date | null;
    email: string;
    twoFactorEnable: boolean;
}

declare interface AuthenticatorInfo{
    secret: string;
    uri: string;
}

declare interface EnableAuthenticator{
    twoFactorCode: string;
}