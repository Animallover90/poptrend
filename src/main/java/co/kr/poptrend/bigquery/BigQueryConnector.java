package co.kr.poptrend.bigquery;

import co.kr.poptrend.error.CustomException;
import co.kr.poptrend.error.ErrorCode;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BigQueryConnector {

    private final GoogleBigqueryConfig bigqueryConfig;

    public BigQuery getBigQuery() {
        try {
            GoogleCredentials credentials = null;
            credentials = ServiceAccountCredentials.fromStream(new ClassPathResource(bigqueryConfig.getBigqueryKeyPath()).getInputStream());

            return BigQueryOptions.newBuilder()
                    .setCredentials(credentials)
                    .setProjectId(bigqueryConfig.getBigqueryProjectId())
                    .setLocation(bigqueryConfig.getBigqueryDatasetLocation())
                    .build()
                    .getService();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.ConnectionError, e);
        }
    }
}