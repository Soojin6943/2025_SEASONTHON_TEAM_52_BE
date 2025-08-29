package com.roommate.roommate.location.service;

import com.roommate.roommate.location.dto.LocationInfo;
import com.roommate.roommate.location.entity.Dong;
import com.roommate.roommate.location.entity.Gu;
import com.roommate.roommate.location.repository.DongRepository;
import com.roommate.roommate.location.repository.GuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoordinateService {
    
    private final GuRepository guRepository;
    private final DongRepository dongRepository;
    
    // 좌표로 구 정보 조회 (Repository의 최적화된 네이티브 쿼리 사용)
    public Gu findGuByCoordinates(double longitude, double latitude) {
        log.info("좌표로 구 검색: ({}, {})", longitude, latitude);
        
        try {
            // Repository의 findOneContainingPoint 메서드 사용 (MySQL 공간 함수 + SRID 4326)
            return guRepository.findOneContainingPoint(longitude, latitude).orElse(null);
            
        } catch (Exception e) {
            log.error("구 검색 중 오류 발생", e);
            return null;
        }
    }
    
    // 좌표로 동 정보 조회 (Repository의 최적화된 네이티브 쿼리 사용)
    public Dong findDongByCoordinates(double longitude, double latitude) {
        log.info("좌표로 동 검색: ({}, {})", longitude, latitude);
        
        try {

            return dongRepository.findOneContainingPoint(longitude, latitude).orElse(null);
            
        } catch (Exception e) {
            log.error("동 검색 중 오류 발생", e);
            return null;
        }
    }
    
    // 동 코드로 해당 동이 속한 구 정보 조회
    public Gu findGuByDongCode(String emdCd) {
        Dong dong = dongRepository.findByEmdCd(emdCd).stream().findFirst().orElse(null);
        if (dong != null) {
            return guRepository.findByBjcd(dong.getBjcd()).stream().findFirst().orElse(null);
        }
        return null;
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
}
