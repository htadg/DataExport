package com.hiten.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Setter
@Getter
@Controller
public class MainController {

    protected static final Log logger = LogFactory.getLog(MainController.class);

    private JobLauncher jobLauncher;
    private Job dataExportJob;

    @RequestMapping("/jobLauncher")
    public void handle() throws Exception{
        MainController.logger.info("Launching Data Export job.");
        JobParameters jobParameters = new JobParameters(getJobParameters());
        jobLauncher.run(dataExportJob, jobParameters);
    }

    public Map<String, JobParameter<?>> getJobParameters() {
        Map<String, JobParameter<?>> jobParameters = new HashMap<>();
        jobParameters.put("sql", new JobParameter<>("select * from cars", String.class, false));
        jobParameters.put("outputFile", new JobParameter<>("/tmp/output.csv", String.class, false));
        jobParameters.put("outputHeaders", new JobParameter<>(List.of("brand", "model", "year"), List.class, false));
        jobParameters.put("dataSourceName", new JobParameter<>("postgres_test", String.class, false));
        return jobParameters;
    }

}
