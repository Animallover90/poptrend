package co.kr.poptrend.bigquery;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GoogleBigqueryConfig {
    @Value("${spring.cloud.gcp.bigquery.credentials.location}")
    private String bigqueryKeyPath;
    @Value("${spring.cloud.gcp.bigquery.dataset-location}")
    private String bigqueryDatasetLocation;
    @Value("${spring.cloud.gcp.bigquery.project-id}")
    private String bigqueryProjectId;
    @Value("${spring.cloud.gcp.bigquery.dataset-name}")
    private String datasetName;
    @Value("${failed_data_save_path}")
    private String failedDataSavePath;
}
