package com.bio.dhh.bio.repository;

import java.sql.Date;

public interface DailyStat {
    Date getDate();
    Integer getCount();
}