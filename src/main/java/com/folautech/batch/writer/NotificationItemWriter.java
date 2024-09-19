package com.folautech.batch.writer;

import com.folautech.batch.entity.notification.Notification;
import com.folautech.batch.entity.notification.NotificationService;
import com.folautech.batch.entity.promotion.Promotion;
import com.folautech.batch.entity.promotion.PromotionDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificationItemWriter implements ItemWriter<List<Notification>> {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void write(Chunk<? extends List<Notification>> chunk) throws Exception {
        log.info("\n\nsaving notifications...");

        for (List<Notification> notifications : chunk.getItems()) {

            log.info("notifications={}", notifications);

            for (Notification notification : notifications){
                log.info("notification={}", notification);
                notificationService.sendPromotionNotification(notification);
            }
        }

        System.out.println("\ndone sending notification!\n\n");
    }
}
