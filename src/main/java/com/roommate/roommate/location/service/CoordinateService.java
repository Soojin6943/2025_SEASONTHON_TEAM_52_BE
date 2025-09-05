package com.roommate.roommate.location.service;

import com.roommate.roommate.location.dto.LocationInfo;
import com.roommate.roommate.location.entity.Dong;
import com.roommate.roommate.location.entity.Gu;
import com.roommate.roommate.location.repository.DongRepository;
import com.roommate.roommate.location.repository.GuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoordinateService {
    
    private final GuRepository guRepository;
    private final DongRepository dongRepository;
    private final Random random = new Random();
    
    // 좌표로 구 정보 조회 (Repository의 최적화된 네이티브 쿼리 사용)
    public Gu findGuByCoordinates(double longitude, double latitude) {
        log.info("좌표로 구 검색: ({}, {})", longitude, latitude);
        
        // 좌표 유효성 검증
        if (longitude < 124.0 || longitude > 132.0 || latitude < 33.0 || latitude > 39.0) {
            throw new RuntimeException("한국 영역 밖의 좌표입니다. (경도: " + longitude + ", 위도: " + latitude + ")");
        }
        
        try {
            // Repository의 findOneContainingPoint 메서드 사용 (MySQL 공간 함수 + SRID 4326)
            return guRepository.findOneContainingPoint(longitude, latitude).orElse(null);
            
        } catch (Exception e) {
            log.error("구 검색 중 오류 발생", e);
            throw new RuntimeException("구 검색 중 오류가 발생했습니다.");
        }
    }
    
    // 좌표로 동 정보 조회 (Repository의 최적화된 네이티브 쿼리 사용)
    public Dong findDongByCoordinates(double longitude, double latitude) {
        log.info("좌표로 동 검색: ({}, {})", longitude, latitude);
        
        // 좌표 유효성 검증
        if (longitude < 124.0 || longitude > 132.0 || latitude < 33.0 || latitude > 39.0) {
            throw new RuntimeException("한국 영역 밖의 좌표입니다. (경도: " + longitude + ", 위도: " + latitude + ")");
        }
        
        try {
            return dongRepository.findOneContainingPoint(longitude, latitude).orElse(null);
            
        } catch (Exception e) {
            log.error("동 검색 중 오류 발생", e);
            throw new RuntimeException("동 검색 중 오류가 발생했습니다.");
        }
    }
    
    // 동 코드로 해당 동이 속한 구 정보 조회
    public Gu findGuByDongCode(String emdCd) {
        if (emdCd == null || emdCd.trim().isEmpty()) {
            throw new RuntimeException("동 코드를 입력해주세요.");
        }
        
        Dong dong = dongRepository.findByEmdCd(emdCd).stream().findFirst().orElse(null);
        if (dong == null) {
            throw new RuntimeException("존재하지 않는 동입니다. (코드: " + emdCd + ")");
        }
        
        Gu gu = guRepository.findByBjcd(dong.getBjcd()).stream().findFirst().orElse(null);
        if (gu == null) {
            throw new RuntimeException("동에 해당하는 구를 찾을 수 없습니다. (동 코드: " + emdCd + ")");
        }
        
        return gu;
    }
    
    // 좌표로 구와 동 정보를 모두 조회 (geom 컬럼 제외)
    public LocationInfo findLocationByCoordinates(double longitude, double latitude) {
        Gu gu = findGuByCoordinates(longitude, latitude);
        Dong dong = findDongByCoordinates(longitude, latitude);
        
        // geom 컬럼을 제외하고 필요한 정보만 포함한 객체 생성
        Gu guInfo = null;
        if (gu != null) {
            guInfo = Gu.builder()
                    .id(gu.getId())
                    .guName(gu.getGuName())
                    .bjcd(gu.getBjcd())
                    .build();
        }
        
        Dong dongInfo = null;
        if (dong != null) {
            dongInfo = Dong.builder()
                    .id(dong.getId())
                    .dongName(dong.getDongName())
                    .emdCd(dong.getEmdCd())
                    .bjcd(dong.getBjcd())
                    .build();
        }
        
        return LocationInfo.builder()
                .gu(guInfo)
                .dong(dongInfo)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
    
    /**
     * 개인정보보호를 위해 좌표에 랜덤 오프셋을 적용합니다.
     * 약 100-200m 범위 내에서 랜덤하게 좌표를 이동시킵니다.
     * 
     * @param longitude 원본 경도
     * @param latitude 원본 위도
     * @return 오프셋이 적용된 좌표 정보
     */
    public LocationInfo applyCoordinateOffset(double longitude, double latitude) {
        // 약 100-200m 범위의 오프셋 (대략 0.001-0.002도)
        double offsetRange = 0.0015; // 약 150m
        
        // -1 ~ 1 사이의 랜덤 값 생성
        double longitudeOffset = (random.nextDouble() - 0.5) * 2 * offsetRange;
        double latitudeOffset = (random.nextDouble() - 0.5) * 2 * offsetRange;
        
        double offsetLongitude = longitude + longitudeOffset;
        double offsetLatitude = latitude + latitudeOffset;
        
        log.info("좌표 오프셋 적용: 원본({}, {}) -> 오프셋({}, {})", 
                longitude, latitude, offsetLongitude, offsetLatitude);
        
        return findLocationByCoordinates(offsetLongitude, offsetLatitude);
    }
}
