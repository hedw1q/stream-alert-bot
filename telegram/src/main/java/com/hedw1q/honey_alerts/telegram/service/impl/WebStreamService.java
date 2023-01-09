package com.hedw1q.honey_alerts.telegram.service.impl;

import com.hedw1q.honey_alerts.share.model.SubscriptionDTO;
import com.hedw1q.honey_alerts.telegram.ex.ApiException;
import com.hedw1q.honey_alerts.telegram.service.StreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WebStreamService implements StreamService {
    @Autowired
    private WebClient webClient;

    @Override
    @Cacheable(value = "subs", key = "#tgId")
    public List<SubscriptionDTO> getUserSubscriptions(Long tgId) {
        return webClient.get()
                .uri("/sub/user/" + tgId)
                .retrieve()
                .bodyToFlux(SubscriptionDTO.class)
                .collectList()
                .onErrorReturn(Collections.emptyList())
                .block();
    }

    @Override
    public List<SubscriptionDTO> getOnlineUserSubscriptions(Long tgId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sub/user/" + tgId)
                        .queryParam("online", true)
                        .build())
                .retrieve()
                .bodyToFlux(SubscriptionDTO.class)
                .collectList()
                .onErrorReturn(Collections.emptyList())
                .block();
    }

    @Override
    @CacheEvict(value = "subs", key = "#sub.user().tgId()")
    public String createNewSubscription(SubscriptionDTO sub) {
        String message;
        try {
            message = webClient.post()
                    .uri("/sub/new")
                    .body(BodyInserters.fromValue(sub))
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new ApiException("Ошибка на стороне сервера")))
                    .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.error(new ApiException("Канал не найден")))
                    .onStatus(HttpStatus.CONFLICT::equals, clientResponse -> Mono.error(new ApiException("Уже подписан")))
                    .bodyToMono(String.class)
                    .onErrorMap(Predicate.not(ApiException.class::isInstance), throwable -> new ApiException("Ошибка соединения с сервером стриминговой платформы"))
                    .block();
        } catch (ApiException apiException) {
            message = apiException.getMessage();
        }

        return message;
    }

    @Override
    @CacheEvict(value = "subs", key = "#sub.user().tgId()")
    public String removeSubscription(SubscriptionDTO sub) {
        String message;
        try {
            message = webClient.post()
                    .uri("/sub/delete")
                    .body(BodyInserters.fromValue(sub))
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new ApiException("Ошибка на стороне сервера")))
                    .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.error(new ApiException("Подписка не найдена")))
                    .bodyToMono(String.class)
                    .onErrorMap(Predicate.not(ApiException.class::isInstance), throwable -> new ApiException("Ошибка соединения с сервером стриминговой платформы"))
                    .block();
        } catch (ApiException apiException) {
            message = apiException.getMessage();
        }
        return message;
    }
}
