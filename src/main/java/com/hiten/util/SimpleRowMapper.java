package com.hiten.util;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleRowMapper implements RowMapper<Map<String, String>> {

    public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, String> result = new HashMap<>();
        ResultSetMetaData rsMetaData = rs.getMetaData();
        List<String> columnNames = new ArrayList<>(rsMetaData.getColumnCount());
        for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
            columnNames.add(rsMetaData.getColumnName(i));
        }
        for (String columnName : columnNames) {
            result.put(columnName, rs.getString(columnName));
        }
        return result;
    }
}
