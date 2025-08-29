package com.roommate.roommate.location.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommate.roommate.location.dto.GeoJsonResponse;
import com.roommate.roommate.location.entity.Dong;
import com.roommate.roommate.location.entity.Gu;
import com.roommate.roommate.location.repository.DongRepository;
import com.roommate.roommate.location.repository.GuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LocationService {
    
    private final GuRepository guRepository;
    private final DongRepository dongRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    
    // 모든 구 데이터를 GeoJSON으로 변환
    public GeoJsonResponse getAllGuGeoJson() {
        String sql = "SELECT gu_name AS name, bjcd AS code, ST_AsGeoJSON(geom) AS geom_json FROM gu";
        return convertToGeoJson(sql, "gu");
    }
    
    // 특정 구 데이터를 GeoJSON으로 변환
    public GeoJsonResponse getGuGeoJsonByBjcd(String bjcd) {
        String sql = "SELECT gu_name AS name, bjcd AS code, ST_AsGeoJSON(geom) AS geom_json FROM gu WHERE bjcd = ?";
        return convertToGeoJson(sql, "gu", bjcd);
    }
    
    // 특정 구의 동 데이터를 GeoJSON으로 변환
    public GeoJsonResponse getDongGeoJsonByBjcd(String bjcd) {
        String sql = "SELECT dong_name AS name, emd_cd AS code, ST_AsGeoJSON(geom) AS geom_json FROM dong WHERE bjcd = ?";
        return convertToGeoJson(sql, "dong", bjcd);
    }
    
    // 특정 동 데이터를 GeoJSON으로 변환
    public GeoJsonResponse getDongGeoJsonByEmdCd(String emdCd) {
        String sql = "SELECT dong_name AS name, emd_cd AS code, ST_AsGeoJSON(geom) AS geom_json FROM dong WHERE emd_cd = ?";
        return convertToGeoJson(sql, "dong", emdCd);
    }
    
    // SQL 쿼리 결과를 GeoJSON으로 변환
    private GeoJsonResponse convertToGeoJson(String sql, String type, Object... params) {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
            List<GeoJsonResponse.Feature> features = new ArrayList<>();
            
            for (Map<String, Object> row : rows) {
                try {
                    String name = (String) row.get("name");
                    String code = (String) row.get("code");
                    String geomJson = (String) row.get("geom_json");
                    
                    if (name != null && code != null && geomJson != null) {
                        // MySQL ST_AsGeoJSON() 결과를 직접 파싱
                        GeoJsonResponse.Geometry geometry = parseGeometryFromJson(geomJson);
                        
                        if (geometry != null) {
                            GeoJsonResponse.Feature feature = GeoJsonResponse.Feature.builder()
                                    .type("Feature")
                                    .properties(GeoJsonResponse.Properties.builder()
                                            .name(name)
                                            .code(code)
                                            .type(type)
                                            .build())
                                    .geometry(geometry)
                                    .build();
                            features.add(feature);
                        } else {
                            System.out.println("Warning: Failed to parse geometry JSON for " + type + ": " + name);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error processing row: " + row + ", Error: " + e.getMessage());
                }
            }
            
            return GeoJsonResponse.builder()
                    .type("FeatureCollection")
                    .features(features)
                    .build();
                    
        } catch (Exception e) {
            System.out.println("Error executing SQL: " + sql + ", Error: " + e.getMessage());
            e.printStackTrace();
            return GeoJsonResponse.builder()
                    .type("FeatureCollection")
                    .features(new ArrayList<>())
                    .build();
        }
    }
    
    // MySQL ST_AsGeoJSON() 결과를 파싱하여 Geometry 객체 생성
    private GeoJsonResponse.Geometry parseGeometryFromJson(String geomJson) {
        try {
            // MySQL ST_AsGeoJSON() 결과를 JsonNode로 파싱
            JsonNode geomNode = objectMapper.readTree(geomJson);
            
            String type = geomNode.get("type").asText();
            JsonNode coordinatesNode = geomNode.get("coordinates");
            
            if ("MultiPolygon".equals(type)) {
                return GeoJsonResponse.Geometry.builder()
                        .type("MultiPolygon")
                        .coordinates(parseCoordinatesFromJsonNode(coordinatesNode))
                        .build();
            } else if ("Polygon".equals(type)) {
                return GeoJsonResponse.Geometry.builder()
                        .type("Polygon")
                        .coordinates(parsePolygonCoordinatesFromJsonNode(coordinatesNode))
                        .build();
            } else {
                System.out.println("Unsupported geometry type: " + type);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error parsing geometry JSON: " + geomJson + ", Error: " + e.getMessage());
            return null;
        }
    }
    
    // MultiPolygon coordinates JsonNode 파싱
    private List<List<List<List<Double>>>> parseCoordinatesFromJsonNode(JsonNode coordinatesNode) {
        List<List<List<List<Double>>>> result = new ArrayList<>();
        
        try {
            if (coordinatesNode.isArray()) {
                for (JsonNode polygonNode : coordinatesNode) {
                    List<List<List<Double>>> polygon = parsePolygonCoordinatesFromJsonNode(polygonNode);
                    if (!polygon.isEmpty()) {
                        result.add(polygon);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing MultiPolygon coordinates: " + e.getMessage());
        }
        
        return result;
    }
    
    // Polygon coordinates JsonNode 파싱
    private List<List<List<Double>>> parsePolygonCoordinatesFromJsonNode(JsonNode coordinatesNode) {
        List<List<List<Double>>> result = new ArrayList<>();
        
        try {
            if (coordinatesNode.isArray()) {
                for (JsonNode ringNode : coordinatesNode) {
                    List<List<Double>> ring = parseRingCoordinatesFromJsonNode(ringNode);
                    if (!ring.isEmpty()) {
                        result.add(ring);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing Polygon coordinates: " + e.getMessage());
        }
        
        return result;
    }
    
    // Ring coordinates JsonNode 파싱
    private List<List<Double>> parseRingCoordinatesFromJsonNode(JsonNode ringNode) {
        List<List<Double>> result = new ArrayList<>();
        
        try {
            if (ringNode.isArray()) {
                for (JsonNode coordNode : ringNode) {
                    if (coordNode.isArray() && coordNode.size() >= 2) {
                        double lng = coordNode.get(0).asDouble();
                        double lat = coordNode.get(1).asDouble();
                        result.add(List.of(lng, lat));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing ring coordinates: " + e.getMessage());
        }
        
        return result;
    }
}
