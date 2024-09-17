package com.folautech.batch.reader;

import com.folautech.batch.config.PromotionBatchConfig;
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
import java.util.Random;

@Service
@Slf4j
public class UserItemReader implements ItemReader<User> {

    @Autowired
    UserRepository userRepository;

    private Iterator<User> userIterator;
    private int pageSize = PromotionBatchConfig.CHUNK_SIZE;
    private int pageNumber = 0;
    Random random = null;

    @PostConstruct
    public void init() {
        random = new Random();
        log.info("UserPromotionItemReader initialized");
    }

    @BeforeChunk
    public void beforeChunk(){
        log.info("beforeChunk ... pageNumber: {}, pageSize: {}", pageNumber, pageSize);

        Pageable pageable =
                PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());

        Page<User> userPage = userRepository.findAll(pageable);

        userIterator = userPage.iterator();
    }

    @AfterChunk
    public void afterChunk(){
        log.info("afterChunk ... pageNumber: {}, pageSize: {}", pageNumber, pageSize);
        pageNumber++;
    }

    @Override
    public User read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        log.info("reading a user...");

        // Return the next user from the iterator
        if (userIterator.hasNext()) {
            User user = userIterator.next();

            if(random.nextBoolean()){
                user.setPromoted(true);
            }

            log.info("User: {}", user);

            return user;
        } else {

            log.info("no more user in the iterator!");
            return null;
        }
    }
}
