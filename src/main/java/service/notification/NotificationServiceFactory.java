package service.notification;

import service.notification.NotificationChannel;
import service.notification.NotificationService;
import service.notification.EmailNotificationService;
import service.notification.SmsNotificationService;
import service.notification.TelegramNotificationService;
import service.notification.FileNotificationService;

/**
 * Фабрика для получения нужной реализации NotificationService по каналу.
 */
public class NotificationServiceFactory {

    /**
     * Возвращает реализацию NotificationService под указанный канал.
     */
    public NotificationService getService(NotificationChannel channel) {
        switch (channel) {
            case EMAIL:
                return new EmailNotificationService();
            case SMS:
                return new SmsNotificationService();
            case TELEGRAM:
                return new TelegramNotificationService();
            case FILE:
                return new FileNotificationService();
            default:
                throw new IllegalArgumentException("Unsupported channel: " + channel);
        }
    }
}