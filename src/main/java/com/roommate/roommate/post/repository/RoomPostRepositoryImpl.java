package com.roommate.roommate.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.roommate.roommate.post.dto.RoomListDto;
import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.entity.QRoomPost;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RoomPostRepositoryImpl implements RoomPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RoomPost> filterPosts(String area, Integer depositMin, Integer depositMax, Integer rentMin, Integer rentMax, List<HouseType> houseTypes, MoveInDate moveInDate, Integer minStayPeriod) {
        QRoomPost rp = QRoomPost.roomPost;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(rp.isRecruiting.eq(true));
        builder.and(rp.area.eq(area));
        if (depositMin != null && depositMin >= 0 && depositMax != null && depositMax >= 0) {
            builder.and(rp.deposit.between(depositMin, depositMax));
        }
        if (rentMin != null && rentMin >= 0 && rentMax != null && rentMax >= 0) {
            builder.and(rp.monthlyRent.between(rentMin, rentMax));
        }
        if (moveInDate != null) {
            builder.and(rp.moveInDate.eq(moveInDate));
        }
        if (minStayPeriod != null) {
            builder.and(rp.minStayPeriod.eq(minStayPeriod));
        }
        if (houseTypes != null && !houseTypes.isEmpty()) {
            builder.and(rp.houseType.in(houseTypes));
        }

        return queryFactory
                .selectFrom(rp)
                .where(builder)
                .orderBy(rp.roomPostId.desc())
                .fetch();
    }
}
