package com.roommate.roommate.location.repository;

import com.roommate.roommate.location.entity.Dong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DongRepository extends JpaRepository<Dong, Long> {

    // bjcd, dongName, emdCd 검색 (JPQL)
    @Query("SELECT d FROM Dong d WHERE d.bjcd = :bjcd")
    List<Dong> findByBjcd(@Param("bjcd") String bjcd);

    @Query("SELECT d FROM Dong d WHERE d.dongName LIKE %:dongName%")
    List<Dong> findByDongNameContaining(@Param("dongName") String dongName);

    @Query("SELECT d FROM Dong d WHERE d.emdCd = :emdCd")
    List<Dong> findByEmdCd(@Param("emdCd") String emdCd);

    // 좌표가 포함되는 동 1건 (MySQL 8+)
    @Query(value = """
        SELECT id, dong_name, emd_cd, bjcd, geom
        FROM dong
        WHERE ST_Contains(
          geom,
          ST_SRID(POINT(:lng, :lat), 4326)
        )
        LIMIT 1
        """, nativeQuery = true)
    Optional<Dong> findOneContainingPoint(@Param("lng") double lng,
                                          @Param("lat") double lat);
}
