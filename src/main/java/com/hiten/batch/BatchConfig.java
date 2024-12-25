package com.hiten.batch;

import com.hiten.config.DataSourceConfig;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@Import(DataSourceConfig.class)
public class BatchConfig extends DefaultBatchConfiguration {

    @Bean
    public Job dataExportJob(JobRepository jobRepository, Step exportStep) {
        return new JobBuilder("Data Export", jobRepository)
                .start(exportStep).build();
    }

    @Bean
    public Step exportStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("Step 1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Hello World");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
