package com.roommate.roommate.location.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoJsonResponse {
    private String type = "FeatureCollection";
    private List<Feature> features;
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Feature {
        private String type = "Feature";
        private Properties properties;
        private Geometry geometry;
    }
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Properties {
        private String name;
        private String code;
        private String type; // "gu" 또는 "dong"
    }
    
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Geometry {
        private String type;
        private Object coordinates; // MultiPolygon 또는 Polygon 모두 지원
    }
}
