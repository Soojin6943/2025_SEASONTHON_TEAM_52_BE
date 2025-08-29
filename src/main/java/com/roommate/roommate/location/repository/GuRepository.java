package com.roommate.roommate.location.repository;

import com.roommate.roommate.location.entity.Gu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuRepository extends JpaRepository<Gu, Long> {

    // bjcd, 이름 검색 (JPQL)
    @Query("SELECT g FROM Gu g WHERE g.bjcd = :bjcd")
    List<Gu> findByBjcd(@Param("bjcd") String bjcd);

    @Query("SELECT g FROM Gu g WHERE g.guName LIKE %:guName%")
    List<Gu> findByGuNameContaining(@Param("guName") String guName);

    // 좌표가 포함되는 구 1건 (MySQL 8+)
    @Query(value = """
        SELECT id, gu_name, bjcd, geom
        FROM gu
        WHERE ST_Contains(
          geom,
          ST_SRID(POINT(:lng, :lat), 4326)
        )
        LIMIT 1
        """, nativeQuery = true)
    Optional<Gu> findOneContainingPoint(@Param("lng") double lng,
                                        @Param("lat") double lat);
}
