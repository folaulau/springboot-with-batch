package com.folautech.batch.processor;

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
public class PromotionItemProcessor implements ItemProcessor<User, Promotion> {

    @BeforeChunk
    public void beforeChunk(){
        log.info("beforeChunk PromotionItemProcessor ...");
    }

    @AfterChunk
    public void afterChunk(){
        log.info("afterChunk PromotionItemProcessor ...");
    }

    @Override
    public Promotion process(User user) throws Exception {
        log.info("processing promotion, user={}\n\n", user.toString());

        if(!user.getPromoted()){
            log.info("User is not promoted, skipping user: {}", user.getEmail());
            return null;
        }

        log.info("User is promoted, creating promotion object for user: {}", user.getEmail());

        return Promotion.builder()
                .user(user)
                .currentYear(LocalDate.now())
                .newYear(LocalDate.now().plusYears(1))
                .currentSalary(100000.0)
                .newSalary(120000.0)
                .build();
    }
}
