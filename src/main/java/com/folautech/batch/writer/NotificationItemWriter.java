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

@Service
@Slf4j
public class NotificationItemWriter implements ItemWriter<Notification> {

    @Autowired
    private NotificationService notificationService;

    @BeforeChunk
    public void beforeChunk(){
        log.info("beforeChunk PromotionItemWriter ...");
    }

    @AfterChunk
    public void afterChunk(){
        log.info("afterChunk PromotionItemWriter ...");
    }

    @Override
    public void write(Chunk<? extends Notification> chunk) throws Exception {
        log.info("\n\nsaving notifications...");

        for (Notification notification : chunk.getItems()) {

            log.info("notification={}", notification);

            notificationService.sendPromotionNotification(notification);

        }

        System.out.println("\ndone sending notification!\n\n");
    }
}
