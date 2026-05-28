package com.nexus.cxm.repository;

import com.nexus.cxm.model.entity.EngagementMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface EngagementMetricRepository extends JpaRepository<EngagementMetric, Long> {

    List<EngagementMetric> findByChannelId(Long channelId);

    @Query("SELECT e FROM EngagementMetric e WHERE " +
           "(:channelId IS NULL OR e.channel.id = :channelId) AND " +
           "(:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate) " +
           "ORDER BY e.date ASC")
    List<EngagementMetric> findWithFilters(
            @Param("channelId") Long channelId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );
}
