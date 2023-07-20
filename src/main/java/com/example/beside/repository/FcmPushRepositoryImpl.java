package com.example.beside.repository;

import com.example.beside.domain.Alarm;
import com.example.beside.domain.AlarmInfo;
import com.example.beside.domain.QAlarm;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public List<Alarm> getAlarmListByType(long user_id, String type) {
        queryFactory = new JPAQueryFactory(em);

        QAlarm qAlarm = QAlarm.alarm;

        List<Alarm> result = queryFactory.selectFrom(qAlarm)
                .where(qAlarm.receive_id.eq(user_id)
                        .and(qAlarm.status.ne(AlarmInfo.ERROR.name()))
                        .and(qAlarm.status.ne(AlarmInfo.DELETE.name()))
                        .and(typeEq(type, qAlarm)))
                .orderBy(qAlarm.alarm_time.desc())
                .fetch();

        return result;
    }

    @Override
    public void updateAlarmStatus(long alarm_id, long user_id, String status) {
        queryFactory = new JPAQueryFactory(em);

        QAlarm qAlarm = QAlarm.alarm;

        queryFactory.update(qAlarm).set(qAlarm.status, status)
                .where(qAlarm.id.eq(alarm_id)
                        .and(qAlarm.receive_id.eq(user_id))).execute();
    }

    private BooleanExpression typeEq(String type, QAlarm qAlarm) {
        BooleanExpression result = type == null ? null :
                type.equals(AlarmInfo.CONFIRM.name()) ? qAlarm.type.eq(AlarmInfo.CONFIRM.name()) :
                        qAlarm.type.eq(AlarmInfo.ACCEPT.name()).or(qAlarm.type.eq(AlarmInfo.INVITE.name())) ;

        return result;
    }

}
