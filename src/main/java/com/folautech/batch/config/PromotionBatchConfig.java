package com.folautech.batch.config;

import com.folautech.batch.entity.notification.Notification;
import com.folautech.batch.entity.promotion.Promotion;
import com.folautech.batch.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
public class PromotionBatchConfig {

    public static final int CHUNK_SIZE = 5;

    @Bean(name = "promotions")
    public Job promotions(JobRepository jobRepository, @Qualifier("loadUsers") Step loadUsers, @Qualifier("sendNotifications") Step sendNotifications) {
        return new JobBuilder("promotions", jobRepository)
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(loadUsers)
                .next(sendNotifications)
                .build();
    }

    @Bean
    public Step loadUsers(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                               ItemReader<User> userItemReader,
                               ItemProcessor<User, Promotion> promotionItemProcessor,
                               ItemWriter<Promotion> promotionItemWriter) {
        log.info("loadUsers...");
        return new StepBuilder("loadUsers", jobRepository).<User, Promotion> chunk(CHUNK_SIZE, transactionManager)
                .reader(userItemReader)
                .processor(promotionItemProcessor)
                .transactionManager(transactionManager)
                .writer(promotionItemWriter)
                .faultTolerant().skipPolicy(new SkipPolicy() {
                    @Override
                    public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
                        if (t instanceof DataIntegrityViolationException) {
                            return true;
                        }

                        if (t instanceof InvalidDataAccessApiUsageException) {
                            return true;
                        }

                        if (t instanceof RuntimeException) {
                            return true;
                        }
                        return false;
                    }
                }).noRetry(DataIntegrityViolationException.class)
                .build();
    }

    @Bean
    public Step sendNotifications(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                  ItemReader<Page<Promotion>> promotionItemReader,
                                  ItemProcessor<Page<Promotion>, List<Notification>> notificationItemProcessor,
                                  ItemWriter<List<Notification>> notificationItemWriter) {
        log.info("sendNotifications...");
        return new StepBuilder("sendNotifications", jobRepository).<Page<Promotion>, List<Notification>> chunk(1, transactionManager)
                .reader(promotionItemReader)
                .processor(notificationItemProcessor)
                .transactionManager(transactionManager)
                .writer(notificationItemWriter)
                .faultTolerant().skipPolicy(new SkipPolicy() {
                    @Override
                    public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
                        if (t instanceof DataIntegrityViolationException) {
                            return true;
                        }

                        if (t instanceof InvalidDataAccessApiUsageException) {
                            return true;
                        }

                        if (t instanceof RuntimeException) {
                            return true;
                        }
                        return false;
                    }
                }).noRetry(DataIntegrityViolationException.class)
                .build();
    }
}
