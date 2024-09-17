package com.folautech.batch.processor;

import com.folautech.batch.entity.notification.Notification;
import com.folautech.batch.entity.promotion.Promotion;
import com.folautech.batch.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class NotificationItemProcessor implements ItemProcessor<Promotion, Notification> {

    @BeforeChunk
    public void beforeChunk(){
        log.info("beforeChunk PromotionItemProcessor ...");
    }

    @AfterChunk
    public void afterChunk(){
        log.info("afterChunk PromotionItemProcessor ...");
    }

    @Override
    public Notification process(Promotion promotion) throws Exception {
        log.info("processing promotion={}\n\n", promotion.toString());

        return Notification.builder()
                .user(promotion.getUser())
                .message("Congratulations! You have been promoted.")
                .build();
    }
}
