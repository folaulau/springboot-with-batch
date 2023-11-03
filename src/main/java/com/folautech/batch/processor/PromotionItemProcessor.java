package com.folautech.batch.processor;

import com.folautech.batch.entity.Promotion;
import com.folautech.batch.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class PromotionItemProcessor implements ItemProcessor<User, Promotion> {

    @Override
    public Promotion process(User user) throws Exception {
        log.info("\n\nprocessing promotion, user={}\n\n", user.toString());
        return Promotion.builder()
                .user(user)
                .currentYear(LocalDate.now())
                .newYear(LocalDate.now().plusYears(1))
                .currentSalary(100000.0)
                .newSalary(120000.0)
                .build();
    }
}
