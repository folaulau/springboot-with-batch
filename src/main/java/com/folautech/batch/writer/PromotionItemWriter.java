package com.folautech.batch.writer;

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
public class PromotionItemWriter implements ItemWriter<Promotion> {

    @Autowired
    private PromotionDAO promotionDAO;

    @BeforeChunk
    public void beforeChunk(){
        log.info("beforeChunk PromotionItemWriter ...");
    }

    @AfterChunk
    public void afterChunk(){
        log.info("afterChunk PromotionItemWriter ...");
    }

    @Override
    public void write(Chunk<? extends Promotion> chunk) throws Exception {
        log.info("\n\nsaving promotions...");

        for (Promotion promotion : chunk.getItems()) {

            log.info("promotion={}", promotion);

            Promotion savedPromotion = promotionDAO.save(promotion);

            log.info("savedPromotion={}", savedPromotion);
        }

        System.out.println("\ndone saving promotions!\n\n");
    }
}
