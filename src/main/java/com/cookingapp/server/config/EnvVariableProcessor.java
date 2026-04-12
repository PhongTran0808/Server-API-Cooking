package com.cookingapp.server.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Xử lý biến môi trường từ .env file.
 * Khi .env có dạng: DB_PASSWORD="${SECURE_DB_PASS}"
 * Processor này strip dấu nháy và resolve tên biến bên trong
 * thành giá trị thật từ biến môi trường hệ thống.
 *
 * Ví dụ:
 *   .env:    DB_PASSWORD="${SECURE_DB_PASS}"
 *   System:  SECURE_DB_PASS=actual_password
 *   Result:  DB_PASSWORD=actual_password
 */
public class EnvVariableProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> resolved = new HashMap<>();

        // Lấy tất cả property sources
        for (var source : environment.getPropertySources()) {
            if (!(source.getSource() instanceof Map)) continue;

            @SuppressWarnings("unchecked")
            Map<String, Object> props = (Map<String, Object>) source.getSource();

            for (Map.Entry<String, Object> entry : props.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                if (!(val instanceof String)) continue;

                String strVal = (String) val;

                // Strip dấu nháy kép bao ngoài: "value" → value
                if (strVal.startsWith("\"") && strVal.endsWith("\"") && strVal.length() > 1) {
                    strVal = strVal.substring(1, strVal.length() - 1);
                }

                // Resolve ${VAR_NAME} bên trong
                if (strVal.startsWith("${") && strVal.endsWith("}")) {
                    String varName = strVal.substring(2, strVal.length() - 1);
                    // Ưu tiên OS environment variable (cho production/CI-CD)
                    String sysVal = System.getenv(varName);
                    // Fallback: System property (được dotenv set từ .env file - cho local dev)
                    if (sysVal == null || sysVal.isEmpty()) {
                        sysVal = System.getProperty(varName);
                    }
                    if (sysVal != null && !sysVal.isEmpty()) {
                        resolved.put(key, sysVal);
                    }
                    // Nếu không tìm thấy -> fallback trong application.properties xử lý
                }
            }
        }

        if (!resolved.isEmpty()) {
            environment.getPropertySources().addFirst(
                new MapPropertySource("envVariableProcessor", resolved)
            );
        }
    }
}
