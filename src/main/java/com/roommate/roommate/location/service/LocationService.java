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
        if (bjcd == null || bjcd.trim().isEmpty()) {
            throw new RuntimeException("구 코드를 입력해주세요.");
        }
        
        String sql = "SELECT gu_name AS name, bjcd AS code, ST_AsGeoJSON(geom) AS geom_json FROM gu WHERE bjcd = ?";
        GeoJsonResponse response = convertToGeoJson(sql, "gu", bjcd);
        
        // 결과가 비어있으면 존재하지 않는 구
        if (response.getFeatures().isEmpty()) {
            throw new RuntimeException("존재하지 않는 구입니다. (코드: " + bjcd + ")");
        }
        
        return response;
    }
    
    // 특정 구의 동 데이터를 GeoJSON으로 변환
    public GeoJsonResponse getDongGeoJsonByBjcd(String bjcd) {
        if (bjcd == null || bjcd.trim().isEmpty()) {
            throw new RuntimeException("구 코드를 입력해주세요.");
        }
        
        String sql = "SELECT dong_name AS name, emd_cd AS code, ST_AsGeoJSON(geom) AS geom_json FROM dong WHERE bjcd = ?";
        GeoJsonResponse response = convertToGeoJson(sql, "dong", bjcd);
        
        // 결과가 비어있으면 존재하지 않는 구
        if (response.getFeatures().isEmpty()) {
            throw new RuntimeException("존재하지 않는 구입니다. (코드: " + bjcd + ")");
        }
        
        return response;
    }
    
    // 특정 동 데이터를 GeoJSON으로 변환
    public GeoJsonResponse getDongGeoJsonByEmdCd(String emdCd) {
        if (emdCd == null || emdCd.trim().isEmpty()) {
            throw new RuntimeException("동 코드를 입력해주세요.");
        }
        
        String sql = "SELECT dong_name AS name, emd_cd AS code, ST_AsGeoJSON(geom) AS geom_json FROM dong WHERE emd_cd = ?";
        GeoJsonResponse response = convertToGeoJson(sql, "dong", emdCd);
        
        // 결과가 비어있으면 존재하지 않는 동
        if (response.getFeatures().isEmpty()) {
            throw new RuntimeException("존재하지 않는 동입니다. (코드: " + emdCd + ")");
        }
        
        return response;
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
    
    // 좌표값으로 구/동 정보 조회
    public LocationData getLocationByCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new RuntimeException("좌표값을 입력해주세요.");
        }
        
        // 먼저 동 정보 조회 (더 정확한 위치)
        String dongSql = "SELECT dong_name, emd_cd, bjcd FROM dong WHERE ST_Contains(geom, ST_GeomFromText(?, 4326)) LIMIT 1";
        String point = String.format("POINT(%f %f)", longitude, latitude);
        
        try {
            List<Map<String, Object>> dongResults = jdbcTemplate.queryForList(dongSql, point);
            
            if (!dongResults.isEmpty()) {
                Map<String, Object> dongRow = dongResults.get(0);
                String dongName = (String) dongRow.get("dong_name");
                String emdCd = (String) dongRow.get("emd_cd");
                String bjcd = (String) dongRow.get("bjcd");
                
                // 구 정보 조회
                String guSql = "SELECT gu_name FROM gu WHERE bjcd = ?";
                List<Map<String, Object>> guResults = jdbcTemplate.queryForList(guSql, bjcd);
                
                if (!guResults.isEmpty()) {
                    String guName = (String) guResults.get(0).get("gu_name");
                    String area = guName + " " + dongName;
                    
                    return new LocationData(guName, dongName, bjcd, emdCd, area);
                }
            }
            
            // 동 정보가 없으면 구 정보만 조회
            String guSql = "SELECT gu_name, bjcd FROM gu WHERE ST_Contains(geom, ST_GeomFromText(?, 4326)) LIMIT 1";
            List<Map<String, Object>> guResults = jdbcTemplate.queryForList(guSql, point);
            
            if (!guResults.isEmpty()) {
                Map<String, Object> guRow = guResults.get(0);
                String guName = (String) guRow.get("gu_name");
                String bjcd = (String) guRow.get("bjcd");
                
                return new LocationData(guName, null, bjcd, null, guName);
            }
            
            throw new RuntimeException("해당 좌표에 대한 지역 정보를 찾을 수 없습니다.");
            
        } catch (Exception e) {
            throw new RuntimeException("좌표 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 지역 정보를 담는 내부 클래스
    public record LocationData(
        String guName,
        String dongName, 
        String bjcd,
        String emdCd,
        String area
    ) {}
}
