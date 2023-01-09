package com.hedw1q.honey_alerts.streamservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.share.model.SubscriptionDTO;
import com.hedw1q.honey_alerts.streamservice.model.Channel;
import com.hedw1q.honey_alerts.streamservice.model.ChannelId;
import com.hedw1q.honey_alerts.streamservice.model.Subscription;
import com.hedw1q.honey_alerts.streamservice.service.StreamService;
import com.hedw1q.honey_alerts.streamservice.service.SubscriptionService;
import com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain.GGStreamList;
import com.hedw1q.honey_alerts.streamservice.service.pubsub.impl.domain.TwitchStreamList;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ApiController.class)
class ApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SubscriptionService service;
    @MockBean
    private StreamService streamService;

    private Subscription sub;

    @BeforeEach
    void setUp() {
        sub = new Subscription(0L, new Channel(new ChannelId("123", StreamPlatform.TWITCH), "name"));
    }

    @Test
    void getUserSubscriptions_properly() throws Exception {
        Mockito.when(service.getSubscriptionsByUserTelegramId(0L)).thenReturn(Collections.singletonList(sub));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/sub/user/0")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(Collections.singletonList(sub.mapToDTO()), objectMapper.readerForListOf(SubscriptionDTO.class).readValue(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    void getUserSubscriptions_online_properly() throws Exception {
        Mockito.when(service.getSubscriptionsByUserTelegramId(0L)).thenReturn(Collections.singletonList(sub));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/sub/user/0")
                        .queryParam("online", "true")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(Collections.emptyList(), objectMapper.readerForListOf(SubscriptionDTO.class).readValue(mvcResult.getResponse().getContentAsString()));
    }

    @Test
    void newSub_properly() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/sub/new")
                        .content(objectMapper.writeValueAsString(sub.mapToDTO()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertFalse(mvcResult.getResponse().getContentAsString().isBlank());
    }

    @Test
    void newSub_AlreadyExist_ThenThrowException() throws Exception {
        Mockito.doThrow(new EntityNotFoundException("notFound")).when(service).createSubscription(any());

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/sub/new")
                        .content(objectMapper.writeValueAsString(sub.mapToDTO()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        assertFalse(mvcResult.getResponse().getContentAsString().isBlank());
    }
    @Test
    void newSub_NullData_ThenThrowValidException() throws Exception {
        Subscription nullSub = new Subscription(null, new Channel(new ChannelId("123", StreamPlatform.TWITCH), "name"));

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/sub/new")
                        .content(objectMapper.writeValueAsString(nullSub.mapToDTO()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        assertFalse(mvcResult.getResponse().getContentAsString().isBlank());
        Mockito.verify(service, Mockito.times(0)).createSubscription(any());
    }

    @Test
    void deleteSub_properly() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/sub/delete")
                        .content(objectMapper.writeValueAsString(sub.mapToDTO()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertFalse(mvcResult.getResponse().getContentAsString().isBlank());
    }

    @Test
    void deleteSub_NotFound_ThenThrowException() throws Exception {
        Mockito.doThrow(new EntityNotFoundException("notFound")).when(service).deleteSubscription(any());

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/sub/delete")
                        .content(objectMapper.writeValueAsString(sub.mapToDTO()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        assertFalse(mvcResult.getResponse().getContentAsString().isBlank());
    }
}