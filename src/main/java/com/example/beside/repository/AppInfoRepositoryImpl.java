package com.example.beside.repository;

import org.springframework.stereotype.Repository;

import com.example.beside.domain.AppInfo;
import com.example.beside.domain.QAppInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AppInfoRepositoryImpl implements AppInfoRepository {

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    @Override
    public AppInfo getAppTermInfo() {
        queryFactory = new JPAQueryFactory(em);
        QAppInfo qAppInfo = QAppInfo.appInfo;

        AppInfo result = queryFactory.select(qAppInfo)
                .from(qAppInfo).fetchFirst();

        return result;
    }

    @Override
    @Transactional
    public void saveAppTermInfo(AppInfo newAppInfo) {
        em.persist(newAppInfo);
        em.flush();
    }

    @Override
    public void updateIosVersion(String version) {
        AppInfo appInfo = em.find(AppInfo.class,1);
        appInfo.updateIosVersion(version);
    }

    @Override
    public void updateAndroidVersion(String version) {
        AppInfo appInfo = em.find(AppInfo.class, 1);
        appInfo.updateAndroidVersion(version);
    }
}
