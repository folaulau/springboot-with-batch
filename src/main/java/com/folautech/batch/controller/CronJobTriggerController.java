package com.folautech.batch.controller;

import com.folautech.batch.cronjob.CronJobName;
import com.folautech.batch.cronjob.JobStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Cronjobs", description = "Cronjob Endpoints")
@RequestMapping("/cronjobs")
@RestController
@Slf4j
public class CronJobTriggerController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("loadTickers")
    private Job loadTickers;

    @Autowired
    @Qualifier("promotions")
    private Job promotions;

    @PostMapping("/run")
    public ResponseEntity<JobStatus> runCronjob(@Parameter(required = true) @RequestParam CronJobName job) {
        log.info("runCronjob, trigger={}", job);


        JobStatus jobStatus = JobStatus.builder()
                .status("running")
                .job(job)
                .build();

        if(job.equals(CronJobName.FETCH_FUTURE_EARNING_REPORTS)){
            JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).addString("uuid", UUID.randomUUID().toString()).toJobParameters();

            try {
                jobLauncher.run(loadTickers, jobParameters);
            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                     JobParametersInvalidException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (job.equals(CronJobName.PROMOTIONS)) {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("chunkSize", Long.valueOf(7))
                    .addLong("time", System.currentTimeMillis())
                    .addString("uuid", UUID.randomUUID().toString()).toJobParameters();

            try {
                jobLauncher.run(promotions, jobParameters);
            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                     JobParametersInvalidException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        return new ResponseEntity<>(jobStatus, OK);
    }
}
