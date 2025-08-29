package com.roommate.roommate.location.dto;

import com.roommate.roommate.location.entity.Dong;
import com.roommate.roommate.location.entity.Gu;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationInfo {
    private Gu gu;
    private Dong dong;
    private double longitude;
    private double latitude;
    
    public String getGuName() {
        return gu != null ? gu.getGuName() : null;
    }
    
    public String getGuCode() {
        return gu != null ? gu.getBjcd() : null;
    }
    
    public String getDongName() {
        return dong != null ? dong.getDongName() : null;
    }
    
    public String getDongCode() {
        return dong != null ? dong.getEmdCd() : null;
    }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (getGuName() != null) {
            sb.append(getGuName());
        }
        if (getDongName() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(getDongName());
        }
        return sb.length() > 0 ? sb.toString() : "위치 정보 없음";
    }
}
