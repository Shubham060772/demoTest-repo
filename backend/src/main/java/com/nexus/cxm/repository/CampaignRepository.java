package com.nexus.cxm.repository;

import com.nexus.cxm.model.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByStatus(Campaign.CampaignStatus status);

    @Query("SELECT c FROM Campaign c WHERE " +
           "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR c.status = :status)")
    List<Campaign> findWithFilters(
            @Param("search") String search,
            @Param("status") Campaign.CampaignStatus status
    );

    long countByStatus(Campaign.CampaignStatus status);

    @Query("SELECT c FROM Campaign c ORDER BY c.impressions DESC")
    List<Campaign> findTopCampaigns(org.springframework.data.domain.Pageable pageable);
}
