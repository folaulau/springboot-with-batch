package com.folautech.batch.config;

import com.folautech.batch.entity.promotion.Security;
import com.folautech.batch.entity.promotion.SecurityFieldSetMapper;
import com.folautech.batch.entity.promotion.SecurityRepository;
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
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class BatchConfig {

    @Bean(name = "loadTickers")
    public Job job(JobRepository jobRepository, @Qualifier("loadSecurities") Step loadSecurities) {
        return new JobBuilder("loadTickers", jobRepository)
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(loadSecurities)
                .build();
    }

    @Bean(name = "tickerFileItemReader")
    public FlatFileItemReader<Security> tickerFileItemReader() {
        log.info("tickerFileItemReader...");
        return new FlatFileItemReaderBuilder<Security>().name("playerFileItemReader")
                .resource(new ClassPathResource("tickers.csv"))
                .delimited()
                .names("ID", "Type", "Ticker", "Name", "Categories", "Description")
                .fieldSetMapper(new SecurityFieldSetMapper())
                .linesToSkip(1)
                .build();
    }

    @Bean("processTickers")
    public ItemProcessor<Security, Security> processTickers() {
        ItemProcessor<Security, Security> processor = new ItemProcessor<Security, Security>() {

            @Override
            public Security process(Security security) throws Exception {

                log.info("security={}", security.toString());

                return security;
            }
        };
        return processor;
    }

    @Bean
    public RepositoryItemWriter<Security> securityItemWriter(@Autowired SecurityRepository securityRepository) {
        log.info("securityItemWriter...");

        RepositoryItemWriterBuilder<Security> builder = new RepositoryItemWriterBuilder<>();
        builder.repository(securityRepository).methodName("saveAndFlush");

        return builder.build();
    }

    @Bean
    public Step loadSecurities(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                               ItemReader<Security> tickerFileItemReader,
                               ItemProcessor<Security, Security> processTickers,
                               ItemWriter<Security> securityItemWriter) {
        log.info("loadSecurities...");
        return new StepBuilder("loadSecurities", jobRepository).<Security, Security>chunk(1, transactionManager)
                .reader(tickerFileItemReader)
                .processor(processTickers)
                .transactionManager(transactionManager)
                .writer(securityItemWriter)
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
