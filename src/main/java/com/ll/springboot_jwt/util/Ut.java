package com.ll.springboot_jwt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

public class Ut {
    public static class json {
    // json 유틸리티 클래스

        public static Object toStr(Map<String, Object> map) {
            try {
                return new ObjectMapper().writeValueAsString(map);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        // Map 객체를 JSON 문자열로 변환하는 메서드
        // ObjectMapper를 사용하여 Map 객체를 JSON 형태의 문자열로 변환
        // 변환 과정에서 예외가 발생하면 null을 반환

        public static Map<String, Object> toMap(String jsonStr) {
            try {
                return new ObjectMapper().readValue(jsonStr, LinkedHashMap.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        // JSON 문자열을 Map 객체로 변환하는 메서드
        // ObjectMapper를 사용하여 JSON 형태의 문자열을 Map 객체로 변환
        // 변환 과정에서 예외가 발생하면 null을 반환
    }
}