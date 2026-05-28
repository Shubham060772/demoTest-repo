package com.nexus.cxm.repository;

import com.nexus.cxm.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
           "(:status IS NULL OR m.status = :status) AND " +
           "(:channelId IS NULL OR m.channel.id = :channelId) AND " +
           "(:customerId IS NULL OR m.customer.id = :customerId) AND " +
           "(:sentiment IS NULL OR m.sentiment = :sentiment) AND " +
           "(:isInbound IS NULL OR m.isInbound = :isInbound) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findWithFilters(
            @Param("status") Message.MessageStatus status,
            @Param("channelId") Long channelId,
            @Param("customerId") Long customerId,
            @Param("sentiment") Message.SentimentType sentiment,
            @Param("isInbound") Boolean isInbound
    );

    long countByStatus(Message.MessageStatus status);
    // count() inherited from JpaRepository

    List<Message> findTop5ByOrderByCreatedAtDesc();

    List<Message> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
