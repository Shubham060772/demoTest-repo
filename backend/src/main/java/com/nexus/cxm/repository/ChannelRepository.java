package com.nexus.cxm.repository;

import com.nexus.cxm.model.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findByIsActiveTrue();
    List<Channel> findAllById(Iterable<Long> ids);
}
