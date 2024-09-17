package com.folautech.batch.reader;

import com.folautech.batch.config.PromotionBatchConfig;
import com.folautech.batch.entity.promotion.Promotion;
import com.folautech.batch.entity.promotion.PromotionRepository;
import com.folautech.batch.entity.user.User;
import com.folautech.batch.entity.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
@Slf4j
public class PromotionItemReader implements ItemReader<Promotion> {

    @Autowired
    private PromotionRepository promotionRepository;

    private Iterator<Promotion> promotionIterator;
    private int pageSize = PromotionBatchConfig.CHUNK_SIZE;
    private int pageNumber = 0;

    @PostConstruct
    public void init() {
        log.info("UserPromotionItemReader initialized");
    }

    @BeforeChunk
    public void beforeChunk(){
        log.info("beforeChunk ... pageNumber: {}, pageSize: {}", pageNumber, pageSize);

        Pageable pageable =
                PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());

        Page<Promotion> userPage = promotionRepository.findAll(pageable);

        promotionIterator = userPage.iterator();
    }

    @AfterChunk
    public void afterChunk(){
        log.info("afterChunk ... pageNumber: {}, pageSize: {}", pageNumber, pageSize);
        pageNumber++;
    }

    @Override
    public Promotion read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        log.info("reading a promotion...");

        // Return the next user from the iterator
        if (promotionIterator.hasNext()) {
            Promotion promotion = promotionIterator.next();

            log.info("Promotion: {}", promotion);

            return promotion;
        } else {

            log.info("no more promotion in the iterator!");
            return null;
        }
    }
}
