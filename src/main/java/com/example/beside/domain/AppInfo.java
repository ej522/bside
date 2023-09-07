package com.example.beside.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "app_info", schema = "bside")
public class AppInfo {

    @Id
    @GeneratedValue
    @Column(name = "app_info_id")
    private long id;

    private String ios_version;

    private String andriod_version;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Column(columnDefinition = "TEXT")
    private String privacy_policy;

    @Column(columnDefinition = "TEXT")
    private String marketing_info;

    @Column(columnDefinition = "TEXT")
    private String withdraw_terms;

    public AppInfo(String ios_versiong, String android_version, String terms,
            String privacy_policy, String marketing_info,
            String withdraw_terms) {
        this.ios_version = ios_versiong;
        this.andriod_version = android_version;
        this.terms = terms;
        this.privacy_policy = privacy_policy;
        this.marketing_info = marketing_info;
        this.withdraw_terms = withdraw_terms;
    }

    public void updateAndroidVersion(String version){
        this.andriod_version = version;
    }

    public void updateIosVersion(String version){
        this.ios_version = version;
    }
}
