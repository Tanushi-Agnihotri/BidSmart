package com.example.BidSmart.notification.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.BidSmart.notification.Notification;
import com.example.BidSmart.notification.NotificationType;

public record NotificationResponse(
    UUID id,
    NotificationType type,
    String title,
    String body,
    boolean read,
    OffsetDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getType(),
            notification.getTitle(),
            notification.getBody(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }
}
