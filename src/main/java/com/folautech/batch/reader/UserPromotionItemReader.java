package com.folautech.batch.reader;

import com.folautech.batch.entity.User;
import com.folautech.batch.entity.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class UserPromotionItemReader implements ItemReader<User> {

    @Autowired
    UserRepository userRepository;

    private Iterator<User> userIterator;

    @PostConstruct
    public void init() {

//        List<User> users = userRepository.findAll();
//
//        log.info("\n\nusers={}\n\n", users);
//        userIterator = users.iterator();
    }

    @Override
    public User read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        log.info("reading users for promotion");

        if (userIterator == null) {
            List<User> users = userRepository.findAll();
            log.info("Users loaded at read time: {}", users);
            userIterator = users.iterator();
        }

        if (userIterator != null && userIterator.hasNext()) {
            return userIterator.next();
        } else {
            return null;
        }
    }
}
