package com.example.beside.repository;

import com.example.beside.domain.Alarm;
import com.example.beside.domain.AlarmInfo;
import com.example.beside.domain.QAlarm;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FcmPushRepositoryImpl implements FcmPushRepository{
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    @Override
    public void insertAlarm(Alarm alarm) {
        em.persist(alarm);
        em.flush();
    }

    @Override
    public List<Alarm> getAlarmAllList(long user_id) {
        queryFactory = new JPAQueryFactory(em);

        QAlarm qAlarm = QAlarm.alarm;

        List<Alarm> result = queryFactory.selectFrom(qAlarm)
                .where(qAlarm.receive_id.eq(user_id)
                        .and(qAlarm.status.ne(AlarmInfo.ERROR.name()))
                        .and(qAlarm.status.ne(AlarmInfo.DELETE.name())))
                .fetch();

        return result;
    }

    @Override
    public List<Alarm> getAlarmByType(long user_id, String type) {
        queryFactory = new JPAQueryFactory(em);

        QAlarm qAlarm = QAlarm.alarm;

        List<Alarm> result = queryFactory.selectFrom(qAlarm)
                .where(qAlarm.receive_id.eq(user_id)
                        .and(qAlarm.status.ne(AlarmInfo.ERROR.name()))
                        .and(qAlarm.status.ne(AlarmInfo.DELETE.name()))
                        .and(qAlarm.type.eq(type)))
                .fetch();

        return result;
    }

    @Override
    public Alarm updateAlarmStatus(long alarm_id, long user_id, String status) {
        queryFactory = new JPAQueryFactory(em);

        QAlarm qAlarm = QAlarm.alarm;

        queryFactory.update(qAlarm).set(qAlarm.status, status)
                .where(qAlarm.id.eq(alarm_id)
                        .and(qAlarm.receive_id.eq(user_id))).execute();

        Alarm result = queryFactory.selectFrom(qAlarm)
                .where(qAlarm.id.eq(alarm_id))
                .fetchOne();

        return result;
    }

}
