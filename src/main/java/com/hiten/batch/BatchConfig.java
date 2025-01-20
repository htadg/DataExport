package com.hiten.batch;

import com.hiten.config.DataSourceConfig;
import com.hiten.config.DataSourceContext;
import com.hiten.util.SimpleBeanWrapperFieldExtractor;
import com.hiten.util.SimpleRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.TaskletStepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


@Configuration
@Import(DataSourceConfig.class)
public class BatchConfig extends DefaultBatchConfiguration {

    @Bean
    public Job dataExportJob(JobRepository jobRepository, Step exportStep, Step setDataSourceContextStep, Step clearDataSourceContextStep) {
        return new JobBuilder("Data Export", jobRepository)
                .start(setDataSourceContextStep)
                .next(exportStep)
                .next(clearDataSourceContextStep)
                .build();
    }

    @Bean
    public Step setDataSourceContextStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("Set Data Source", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
                    String dataSourceName = jobParameters.getString("dataSourceName");
                    DataSourceContext.setDataSourceName(dataSourceName);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step clearDataSourceContextStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("Clear Data Source", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    DataSourceContext.clearDataSourceName();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step exportStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcCursorItemReader<Map<String, String>> itemReader, FlatFileItemWriter<Map<String, String>> itemWriter) {
        return new StepBuilder("Export from DB", jobRepository)
                .<Map<String, String>, Map<String, String>>chunk(10, transactionManager)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Map<String, String>> itemReader (@Value("#{jobParameters[sql]}") String sql) {
        String dataSourceName = DataSourceContext.getDataSourceName();
        DataSource applicationDataSource = DataSourceConfig.getDataSource(dataSourceName);
        Assert.notNull(applicationDataSource, "applicationDataSource must not be null");
        return new JdbcCursorItemReaderBuilder<Map<String, String>>().name("itemReader")
                .dataSource(applicationDataSource)
                .sql(sql)
                .rowMapper(new SimpleRowMapper())
                .build();

    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Map<String, String>> itemWriter(@Value("#{jobParameters[outputFile]}") String outputFile, @Value("#{jobParameters[outputHeaders]}") List<String> outputHeaders) {
        WritableResource resource = new FileSystemResource(outputFile);

        SimpleBeanWrapperFieldExtractor fieldExtractor = new SimpleBeanWrapperFieldExtractor();
        fieldExtractor.setNames(outputHeaders.toArray(new String[0]));
        fieldExtractor.afterPropertiesSet();

        return new FlatFileItemWriterBuilder<Map<String, String>>().name("itemWriter")
                .resource(resource)
                .delimited()
                .delimiter("|")
                .fieldExtractor(fieldExtractor)
                .headerCallback(writer -> writer.write(String.join("|", outputHeaders)))
                .build();
    }

}
