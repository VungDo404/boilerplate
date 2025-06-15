import { Injectable } from '@angular/core';
import { AccountService as AS } from "../../../../shared/service/http/account.service";
import { SessionService } from "../../../../shared/service/session.service";
import { finalize, forkJoin } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    phoneNumber!: string;
    gender!: number | null;
    displayName!: string;
    dateOfBirth!: Date | null;

    constructor(private accountService: AS, private sessionService: SessionService) {}

    addFormInitialValue(cb: (phoneNumber: string, gender: number | null, displayName: string, dateOfBirth: Date | null) => void) {
        this.accountService.getUpdateUserInfo(this.sessionService.id).subscribe({
            next: (response) => {
                this.phoneNumber = response.phoneNumber ?? '';
                this.gender = response.gender !== undefined ? response.gender : null;
                this.displayName = response.displayName ?? '';
                this.dateOfBirth = response.dateOfBirth ? response.dateOfBirth : null;
                cb(
                    this.phoneNumber,
                    this.gender,
                    this.displayName,
                    this.dateOfBirth
                );

            }
        })
    }

    updateUserInfo(body: UpdateUserInfo, cb: () => void, avatar?: File) {
        const observables = [];

        const hasChanges =
            !!(body.phoneNumber && body.phoneNumber !== this.phoneNumber) ||
            (body.gender !== undefined && body.gender !== this.gender) ||
            !!(body.displayName && body.displayName !== this.displayName) ||
            !!(body.dateOfBirth && !this.compareDate(body.dateOfBirth, this.dateOfBirth));

        if (hasChanges) {
            observables.push(this.accountService.updateUserInfo(this.sessionService.id,body));
        }

        if (avatar) {
            observables.push(this.accountService.avatar(this.sessionService.id,avatar));
        }

        forkJoin(observables).pipe(finalize(cb)).subscribe({
            next: () => {
                this.phoneNumber = body.phoneNumber ?? this.phoneNumber;
                this.gender = body.gender ?? this.gender;
                this.displayName = body.displayName ?? this.displayName;
                this.dateOfBirth = body.dateOfBirth ?? this.dateOfBirth;
            }
        })
    }

    private compareDate(d1: Date | null | undefined, d2: Date | null | undefined){
        if (!d1 && !d2) return true;
        if (!d1 || !d2) return false;

        return d1.getFullYear() === d2.getFullYear()
            && d1.getMonth() === d2.getMonth()
            && d1.getDate() === d2.getDate();
    }
}
