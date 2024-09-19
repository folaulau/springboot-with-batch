package com.folautech.batch.processor;

import com.folautech.batch.entity.notification.Notification;
import com.folautech.batch.entity.promotion.Promotion;
import com.folautech.batch.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NotificationItemProcessor implements ItemProcessor<Page<Promotion>, List<Notification>> {

    @BeforeChunk
    public void beforeChunk(){
        log.info("beforeChunk PromotionItemProcessor ...");
    }

    @AfterChunk
    public void afterChunk(){
        log.info("afterChunk PromotionItemProcessor ...");
    }

    @Override
    public List<Notification> process(Page<Promotion> promotionPage) throws Exception {
        log.info("processing promotions={}\n\n", promotionPage.toString());

        if(!promotionPage.hasContent()){
            return null;
        }

        List<Notification> notifications = new ArrayList<>();

        for (Promotion promotion : promotionPage.getContent()) {
            log.info("processing promotion, promotion={}\n\n", promotion.toString());
            notifications.add(Notification.builder()
                    .user(promotion.getUser())
                    .message("Congratulations! You have been promoted.")
                    .build());
        }

        return notifications;
    }
}
