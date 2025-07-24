declare interface Notification{
    id: number;
    title: string;
    message: string;
    type: NotificationType;
    url: string;
    notificationTopic: NotificationTopic;
    createdAt: Date;
}

declare type NotificationType = 'INFO' | 'ERROR' | 'ALERT' | 'WARNING';

declare interface NotificationTopic {
    id: number;
    name: string;
}