package com.folautech.batch.config;

import com.folautech.batch.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
//@EnableBatchProcessing
public class PromotionBatchConfig {

    public static final int CHUNK_SIZE = 5;

//    @Autowired
//    private SecurityDAO securityDAO;
//
//    @Autowired SecurityRepository securityRepository;

//    @Autowired
//    @Qualifier("processStocks")
//    private ItemProcessor<Security, Transaction> processStocks;

    @Bean(name = "promotions")
    public Job promotions(JobRepository jobRepository, @Qualifier("loadPromotions") Step loadPromotions) {
        return new JobBuilder("promotions", jobRepository)
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(loadPromotions)
                .build();
    }

    @Bean
    public Step loadPromotions(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                               ItemReader<User> userPromotionItemReader,
                               ItemProcessor<User, Promotion> promotionItemProcessor,
                               ItemWriter<Promotion> promotionItemWriter) {
        log.info("loadPromotions...");
        return new StepBuilder("loadPromotions", jobRepository).<User, Promotion> chunk(CHUNK_SIZE, transactionManager)
                .reader(userPromotionItemReader)
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

//    @Bean(name = "tickerFileItemReader")
////    @JobScope
//    public FlatFileItemReader<Security> tickerFileItemReader() {
//        log.info("tickerFileItemReader...");
//        return new FlatFileItemReaderBuilder<Security>().name("playerFileItemReader")
//                .resource(new ClassPathResource("tickers.csv"))
//                .delimited()
//                .names("ID", "Type", "Ticker", "Name", "Categories", "Description")
//                .fieldSetMapper(new SecurityFieldSetMapper())
//                .linesToSkip(1)
//                .build();
//    }

//    @Bean("processTickers")
////    @JobScope
//    public ItemProcessor<Security, Security> processTickers() {
//        ItemProcessor<Security, Security> processor = new ItemProcessor<Security, Security>() {
//
//            @Override
//            public Security process(Security security) throws Exception {
//
//                log.info("security={}", security.toString());
//
//                return security;
//            }
//        };
//        return processor;
//    }
//
//    @Bean
//    public RepositoryItemWriter<Security> securityItemWriter(@Autowired SecurityRepository securityRepository) {
//        log.info("securityItemWriter...");
//
//        RepositoryItemWriterBuilder<Security> builder = new RepositoryItemWriterBuilder<>();
//        builder.repository(securityRepository).methodName("saveAndFlush");
//
//        return builder.build();
//    }
}
