package org.n1vnhil.framework.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.registerModules(new JavaTimeModule());
    }

    /**
     *  将对象转换为 JSON 字符串
     * @param obj
     * @return
     */
    @SneakyThrows
    public static String toJsonString(Object obj) {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

    /**
     * 初始化：统一使用 Spring Boot 个性化配置的 ObjectMapper
     *
     * @param objectMapper
     */
    public static void init(ObjectMapper objectMapper) {
        OBJECT_MAPPER = objectMapper;
    }

    /**
     * 将 Json 字符串转换为对象
     * @return
     * @param <T>
     */
    @SneakyThrows
    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        if(StringUtils.isBlank(jsonStr)) return null;
        return OBJECT_MAPPER.readValue(jsonStr, clazz);
    }

    /**
     * JSON 字符串解析为 Map
     * @param jsonStr
     * @param keyClazz
     * @param valueClazz
     * @return
     * @param <K>
     * @param <V>
     */
    public static <K, V> Map<K, V> parseMap(String jsonStr, Class<K> keyClazz, Class<V> valueClazz) throws Exception {
        TypeReference<Map<K, V>> typeRef = new TypeReference<Map<K, V>>() {
        };
        return OBJECT_MAPPER.readValue(jsonStr, OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, keyClazz, valueClazz));
    }

    /**
     * JSON 字符串解析为 List
     * @param jsonStr
     * @param clazz
     * @return
     * @param <T>
     * @throws JsonProcessingException
     */
    public static <T> List<T> parseList(String jsonStr, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<List<T>>() {
            @Override
            public Type getType() {
                return OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            }
        });
    }
}