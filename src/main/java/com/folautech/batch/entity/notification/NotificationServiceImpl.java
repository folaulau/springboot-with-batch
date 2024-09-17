package com.folautech.batch.entity.notification;

import com.folautech.batch.entity.promotion.Promotion;
import com.folautech.batch.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public Notification sendPromotionNotification(Notification notification) {
        log.info("Sending promotion notification to user: {}, msg: {}", notification.getUser().getEmail(), notification.getMessage());
        return notificationRepository.saveAndFlush(notification);
    }
}
