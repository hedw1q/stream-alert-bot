package com.hedw1q.honey_alerts.streamservice.repository;

import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import com.hedw1q.honey_alerts.streamservice.model.StreamStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface StreamRepository extends JpaRepository<Stream, String> {
    @EntityGraph(value = "Stream.channel")
    List<Stream> findAllByStreamStatusAndChannel(StreamStatus streamStatus, Channel channel);

    @EntityGraph(value = "Stream.channel")
    List<Stream> findAllByStreamStatus(StreamStatus streamStatus);

    @EntityGraph(value = "Stream.channel")
    Optional<Stream> findByChannel(Channel channel);

    @Query(value = "UPDATE streams " +
            "SET stream_finish_time=:time, stream_status='OFFLINE' " +
            "WHERE id=:id", nativeQuery = true)
    @Modifying
    @Transactional
    void updateStreamSetOfflineById(@Param("time") Instant streamFinishTime, @Param("id") String streamId);

    @Query(value = """
            UPDATE Stream s
            SET s.viewerCount=?1
            WHERE s.id=?2
            """)
    @Modifying
    @Transactional
    void updateViewerCount(Integer viewerCount, String streamId);
}
