package com.hedw1q.honey_alerts.streamservice.service.impl;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.Stream;
import com.hedw1q.honey_alerts.streamservice.model.StreamStatus;
import com.hedw1q.honey_alerts.streamservice.repository.StreamRepository;
import com.hedw1q.honey_alerts.streamservice.service.StreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StreamServiceImpl implements StreamService {
    @Autowired
    private StreamRepository repository;

    @Override
    public Stream saveStream(Stream stream) {
//        List<Stream> currentLiveStreams = repository.findAllByStreamStatusAndChannel(StreamStatus.LIVE, stream.getChannel());
//        if (!currentLiveStreams.isEmpty()) {
//            for (Stream s : currentLiveStreams) {
//                setStreamOfflineById(s.getId());
//            }
//        }
        return repository.save(stream);
    }

    @Override
    public void setStreamOfflineById(String streamId) {
        repository.updateStreamSetOfflineById(Instant.now(), streamId);
    }

    @Override
    public void updateViewerCount(int viewerCount, String streamId) {
        repository.updateViewerCount(viewerCount, streamId);
    }

    @Override
    public Optional<Stream> findCurrentLiveStreamByChannel(Channel channel) {
        List<Stream> currentLiveStreams = repository.findAllByStreamStatusAndChannel(StreamStatus.LIVE, channel);

        if (currentLiveStreams.isEmpty()) return Optional.empty();
        else return Optional.of(currentLiveStreams.get(0));
    }

    @Override
    public List<Stream> getLiveStreams() {
        return repository.findAllByStreamStatus(StreamStatus.LIVE);
    }

    @Override
    public List<Stream> getLiveStreamsByPlatform(StreamPlatform streamPlatform) {
        return repository.findAllByStreamStatus(StreamStatus.LIVE).stream()
                .filter(x -> x.getChannel().getId().getPlatform().equals(streamPlatform))
                .toList();
    }

    @Override
    public boolean isChannelOnline(Channel channel) {
        Optional<Stream> stream = repository.findByChannel(channel);

        return stream.isPresent() && stream.get().getStreamStatus() == StreamStatus.LIVE;
    }
}
