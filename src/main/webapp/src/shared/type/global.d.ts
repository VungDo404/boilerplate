declare interface ProblemDetail {
    type: string;
    title: string;
    status: number;
    detail: string;
    instance: string;
    timestamp: Date;
}

declare interface FieldError {
    objectName: string;
    field: string;
    message: string;
}

declare interface ProblemDetailWithFieldError extends ProblemDetail {
    fieldErrors: FieldError[];
}

type BooleanMap = Record<string, boolean>;

