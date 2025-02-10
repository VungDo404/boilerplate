declare interface AuthenticationResult {
    accessToken: string;
    encryptedAccessToken: string;
    expiresInSeconds: number;
    shouldChangePasswordOnNextLogin: boolean;
    passwordResetCode?: string;
    isTwoFactorEnabled: boolean;
    requiresTwoFactorVerification: boolean;
}