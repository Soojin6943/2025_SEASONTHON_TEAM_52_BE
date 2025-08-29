package com.roommate.roommate.location.controller;

import com.roommate.roommate.location.dto.GeoJsonResponse;
import com.roommate.roommate.location.dto.LocationInfo;
import com.roommate.roommate.location.entity.Dong;
import com.roommate.roommate.location.entity.Gu;
import com.roommate.roommate.location.service.LocationService;
import com.roommate.roommate.location.service.CoordinateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Location")
@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final CoordinateService coordinateService;

    // ===== 폴리곤 데이터 조회 API =====

    @Operation(summary = "특정 구 폴리곤 조회")
    @GetMapping("/gu/{bjcd}")
    public ResponseEntity<GeoJsonResponse> getGuByBjcd(@PathVariable String bjcd) {
        return ResponseEntity.ok(locationService.getGuGeoJsonByBjcd(bjcd));
    }

    @Operation(summary = "특정 구의 동 폴리곤 조회")
    @GetMapping("/dong/{bjcd}")
    public ResponseEntity<GeoJsonResponse> getDongByBjcd(@PathVariable String bjcd) {
        return ResponseEntity.ok(locationService.getDongGeoJsonByBjcd(bjcd));
    }

    @Operation(summary = "특정 동 폴리곤 조회")
    @GetMapping("/dong/emd/{emdCd}")
    public ResponseEntity<GeoJsonResponse> getDongByEmdCd(@PathVariable String emdCd) {
        return ResponseEntity.ok(locationService.getDongGeoJsonByEmdCd(emdCd));
    }

    // ===== 좌표 기반 지역 정보 조회 API =====

    @Operation(summary = "좌표로 구와 동 정보 조회")
    @GetMapping("/coordinates")
    public ResponseEntity<LocationInfo> getLocationByCoordinates(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        LocationInfo locationInfo = coordinateService.findLocationByCoordinates(longitude, latitude);
        if (locationInfo.getGu() == null && locationInfo.getDong() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(locationInfo);
    }

    @Operation(summary = "좌표로 구 정보만 조회")
    @GetMapping("/coordinates/gu")
    public ResponseEntity<LocationInfo> getGuByCoordinates(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        Gu gu = coordinateService.findGuByCoordinates(longitude, latitude);
        if (gu == null) {
            return ResponseEntity.notFound().build();
        }

        LocationInfo locationInfo = LocationInfo.builder()
                .gu(gu)
                .longitude(longitude)
                .latitude(latitude)
                .build();
        return ResponseEntity.ok(locationInfo);
    }

    @Operation(summary = "좌표로 동 정보만 조회")
    @GetMapping("/coordinates/dong")
    public ResponseEntity<LocationInfo> getDongByCoordinates(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        Dong dong = coordinateService.findDongByCoordinates(longitude, latitude);
        if (dong == null) {
            return ResponseEntity.notFound().build();
        }

        // 동이 속한 구 정보도 함께 조회
        Gu gu = coordinateService.findGuByDongCode(dong.getEmdCd());

        LocationInfo locationInfo = LocationInfo.builder()
                .gu(gu)
                .dong(dong)
                .longitude(longitude)
                .latitude(latitude)
                .build();
        return ResponseEntity.ok(locationInfo);
    }
}
