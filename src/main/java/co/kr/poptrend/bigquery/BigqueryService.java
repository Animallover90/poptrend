package co.kr.poptrend.bigquery;

import com.google.cloud.bigquery.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BigqueryService {

    private final BigQueryConnector bigQueryConfiguration;
    private final GoogleBigqueryConfig bigqueryConfig;

    public Map<String, Object> insertDataInBigquery(List<Map<String, Object>> data, String tableName) {
        Map<String, Object> outMap = new HashMap<>();
        BigQuery bigQuery = bigQueryConfiguration.getBigQuery();
        String datasetName = bigqueryConfig.getDatasetName();
        String lowCaseTableName = tableName.toLowerCase();

        Set<String> columnSet = new HashSet<>();
        for (Map<String, Object> row : data) {
            // 컬럼명을 얻기 위해
            columnSet.addAll(row.keySet());
        }

        // 테이블이 없을 경우 columnSet 그대로 반환
        // 필요한 컬럼이 있는지 확인하고 추가
        Set<String> necessaryColumn = necessaryColumnSet(bigQuery, lowCaseTableName, datasetName, columnSet);

        try {
            if (!isExistTable(bigQuery, lowCaseTableName, datasetName)) {
                createTable(bigQuery, lowCaseTableName, datasetName, necessaryColumn);
            } else if (!necessaryColumn.isEmpty()) {
                createColumn(bigQuery, lowCaseTableName, datasetName, necessaryColumn);
            }
        } catch (Exception e) {
            mapToJsonFormatFile(data);
        }

        insertData(bigQuery, lowCaseTableName, datasetName, data);

        return outMap;
    }

    public boolean isExistTable(BigQuery bigQuery, String tableName, String dataset) {
        boolean output = false;
        Table table = bigQuery.getDataset(dataset).get(tableName);
        if (table != null && table.exists()) {
            output = true;
        }

        return output;
    }

    private void createTable(BigQuery bigQuery, String tableName, String dataset, Set<String> columns) {
        List<Field> fieldList = new ArrayList<>();
        for (String column : columns) {
            fieldList.add(Field.of(column, StandardSQLTypeName.STRING));
        }
        Schema schema = Schema.of(fieldList);
        TableId tableId = TableId.of(dataset, tableName);
        TableDefinition tableDefinition = StandardTableDefinition.of(schema);
        TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();

        bigQuery.create(tableInfo).update();
    }

    private Set<String> necessaryColumnSet(BigQuery bigQuery, String tableName, String dataset, Set<String> columnSet) {
        Set<String> set = new HashSet<>();

        if (!isExistTable(bigQuery, tableName, dataset)) {
            return columnSet;
        }

        FieldList fields = bigQuery.getTable(TableId.of(dataset, tableName)).getDefinition().getSchema().getFields();

        for (String columnName : columnSet) {
            int count = 0;
            for (Field field : fields) {
                if (field.getName().equals(columnName)) {
                    count++;
                }
            }
            if (count == 0) {
                set.add(columnName);
            }
        }

        return set;
    }

    private void createColumn(BigQuery bigQuery, String tableName, String dataset, Set<String> columns) {
        TableId tableId = TableId.of(dataset, tableName);
        Table table = bigQuery.getTable(tableId);
        TableDefinition tableDefinition = table.getDefinition();
        Schema schema = tableDefinition.getSchema();
        List<Field> fieldList = new ArrayList<>(schema.getFields());
        for (String column : columns) {
            fieldList.add(Field.of(column, StandardSQLTypeName.STRING));
        }
        Schema newSchema = Schema.of(fieldList);

        table.toBuilder().setDefinition(StandardTableDefinition.of(newSchema)).build().update();
    }

    private void insertData(BigQuery bigQuery, String tableName, String dataset, List<Map<String, Object>> data) {
        try {
            int dataSize = data.size();
            int ROW_SIZE = 500;
            List<Integer> insertRowSizeList = new ArrayList<>();
            while (dataSize > 0) {
                if (dataSize - ROW_SIZE > 0) {
                    insertRowSizeList.add(ROW_SIZE);
                } else {
                    insertRowSizeList.add(dataSize);
                }
                dataSize -= ROW_SIZE;
            }

            int startRow = 0;
            for (int insertSize : insertRowSizeList) {
                TableId tableId = TableId.of(dataset, tableName);
                InsertAllRequest.Builder request = InsertAllRequest.newBuilder(tableId);
                for (int i = 0; i < insertSize; i++) {
                    request.addRow(data.get(startRow + i));
                }
                startRow += insertSize;

                InsertAllResponse response = bigQuery.insertAll(request.build());

                if (response.hasErrors()) {
                    // 컬럼, 테이블 생성시 바로 적용이 안되는 경우가 많아서 새로운 연결을 이용하여 insert들 시도(성공하기도 하고 실패하기도 함)
                    BigQuery newBigQuery = bigQueryConfiguration.getBigQuery();

                    response = newBigQuery.insertAll(request.build());

                    if (response.hasErrors()) {
                        mapToJsonFormatFile(data);
                    }
                }
            }
        } catch (Exception e) {
            mapToJsonFormatFile(data);
        }
    }

    private void mapToJsonFormatFile(List<Map<String, Object>> data) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> map : data) {
                JSONObject json = new JSONObject();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    json.put(key, value);
                }
                jsonArray.add(json);
            }

            String format = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
            BufferedWriter jsonFile = new BufferedWriter(new FileWriter(bigqueryConfig.getFailedDataSavePath() + format + ".log", true));
            jsonFile.write(jsonArray.toJSONString());
            jsonFile.newLine();
            jsonFile.flush();
            jsonFile.close();
        } catch (IOException e) {
            log.error("IOError 저장 실패 Error_reason = " + e);
        }
    }
}
