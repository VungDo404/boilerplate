import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { ToastService } from "../../service/toast.service";
import bytes from 'bytes';
import { takeUntil } from "rxjs";
import { TranslateService } from "@ngx-translate/core";
import { BaseComponent } from "../base.component";

@Component({
    selector: 'app-upload',
    imports: [],
    templateUrl: './upload.component.html',
    standalone: true,
    styleUrl: './upload.component.scss'
})
export class UploadComponent extends BaseComponent {
    @Input() multiple: boolean = false;
    @Input() accept: string = "image/*";
    @Input() size!: string | number | undefined;

    @Output() onFileUpload = new EventEmitter<File[]>();
    @ViewChild('file') fileInput!: ElementRef<HTMLInputElement>;

    constructor(private toastService: ToastService, private translate: TranslateService) {
        super();
    }

    trigger() {
        this.fileInput.nativeElement.click();
    }

    protected onFileChange(event: Event) {
        const inputElement = event!.target as HTMLInputElement;
        if (inputElement.files && inputElement.files.length > 0) {
            const files: FileList = inputElement.files;
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                if (this.size) {
                    const maxSize = bytes.parse(this.size);
                    if (maxSize && file.size > maxSize) {
                        const formatted = bytes.format(maxSize, { unit: 'KB' });
                        if (formatted) {
                            this.translate.get(['FileTooLarge', 'FileTooLargeDetail'], { value: formatted })
                                .pipe(takeUntil(this.destroy$))
                                .subscribe(translations => {
                                    this.toastService.push(
                                        'error',
                                        translations['FileTooLarge'],
                                        translations['FileTooLargeDetail'],
                                        'top-right'
                                    )
                                });
                        }
                        this.clearFileInput();
                        return;
                    }
                }
                if (this.accept) {
                    const fileType = file.type.toLowerCase();
                    const acceptArr = this.accept.split(',').map(type => type.trim().toLowerCase());
                    if (!this.isAcceptedFileType(fileType, acceptArr)) {
                        this.translate.get(['FileTypeNotSupported', 'FileTypeNotSupportedDetail'], { value: this.accept })
                            .pipe(takeUntil(this.destroy$))
                            .subscribe(translations => {
                                this.toastService.push(
                                    'error',
                                    translations['FileTypeNotSupported'],
                                    translations['FileTypeNotSupportedDetail']
                                )
                            });
                        this.clearFileInput();
                        return;
                    }
                }
            }
            this.onFileUpload.emit(Array.from(files));
            this.clearFileInput();
        }

    }

    private clearFileInput(): void {
        if (this.fileInput && this.fileInput.nativeElement) {
            this.fileInput.nativeElement.value = '';
        }
    }

    private isAcceptedFileType(fileType: string, acceptArr: string[]){
        return acceptArr.some(acceptType => {
            if (acceptType === '*/*') return true;
            if (acceptType.endsWith('/*')) {
                const baseType = acceptType.split('/')[0];
                return fileType.startsWith(baseType + '/');
            }
            return fileType === acceptType;
        });
    }

}
