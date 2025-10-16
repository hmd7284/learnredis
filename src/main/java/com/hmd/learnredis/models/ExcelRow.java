package com.hmd.learnredis.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ExcelRow {
    private Map<String, Object> data = new HashMap<>();

    public Object get(String key) {
        return data.get(key);
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public String getString(String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : "";
    }

    public Long getLong(String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                String str = ((String) value).replace(",", "");
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Double getDouble(String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString().replace(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public BigDecimal getBigDecimal(String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof BigDecimal)
            return (BigDecimal) value;
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue()).stripTrailingZeros();
        }

        if (value instanceof String) {
            try {
                return new BigDecimal(((String) value).replace(",", ""));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public LocalDateTime getLocalDateTime(String key) {
        Object value = data.get(key);
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String str = value.toString().trim();
        return "true".equals(str) || "yes".equals(str) || "1".equals(str);
    }
}
