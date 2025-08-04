declare interface Notification{
    id: number;
    title: string;
    message: string;
    type: NotificationType;
    url?: string;
    notificationTopicModel?: NotificationTopic;
    createdAt: Date;
    isRead: boolean;
}

declare type NotificationType = 'INFO' | 'ERROR' | 'ALERT' | 'WARNING';

declare interface NotificationTopic {
    id: number;
    name: string;
    muted: boolean;
}