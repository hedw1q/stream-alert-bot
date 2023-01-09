package com.hedw1q.honey_alerts.telegram.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedw1q.honey_alerts.share.model.TelegramApiMessage;
import com.hedw1q.honey_alerts.telegram.bot.TgBot;
import com.hedw1q.honey_alerts.telegram.model.User;
import com.hedw1q.honey_alerts.telegram.service.UserService;
import org.junit.jupiter.api.AfterEach;
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

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
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
    private TgBot tgBot;
    @MockBean
    private UserService userService;
    private User user;
    private TelegramApiMessage telegramApiMessage;


    @BeforeEach
    void setUp() {
        user = new User(0L, "tgName", true, false, null);
        Mockito.when(userService.findByTelegramId(any())).thenReturn(user);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void should_NOT_sentMainNotification_when_InvalidInput() throws Exception {
        telegramApiMessage = new TelegramApiMessage(Collections.emptyList(), null, "https://static-cdn.jtvnw.net/previews-ttv/live_user_c_a_k_e-1920x1080.jpg");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/send/main")
                        .content(objectMapper.writeValueAsString(telegramApiMessage))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void should_sentMainNotification_withAttachment() throws Exception {
        telegramApiMessage = new TelegramApiMessage(Arrays.asList(1L, 1L, 298391802L), "message", "https://static-cdn.jtvnw.net/previews-ttv/live_user_c_a_k_e-1920x1080.jpg");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/send/main")
                        .content(objectMapper.writeValueAsString(telegramApiMessage))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(tgBot, Mockito.times(2)).sendAttachmentMessageToChannel( any(), eq(telegramApiMessage.attachmentURL()), eq(telegramApiMessage.message()));
    }

    @Test
    void should_sentMainNotification_withoutAttachment() throws Exception {
        telegramApiMessage = new TelegramApiMessage(Arrays.asList(1L, 1L, 298391802L), "message", null);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/send/main")
                        .content(objectMapper.writeValueAsString(telegramApiMessage))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(tgBot, Mockito.times(2)).sendTextMessageToChannel(anyLong(), eq(telegramApiMessage.message()), anyBoolean());
        Mockito.verify(tgBot, Mockito.times(0)).sendAttachmentMessageToChannel(any(), eq(telegramApiMessage.attachmentURL()), eq(telegramApiMessage.message()));
    }

    @Test
    void should_NOT_sentAdditionalNotification_whenDisabled_withAttachment() throws Exception {
        telegramApiMessage = new TelegramApiMessage(Arrays.asList(1L, 1L, 298391802L), "message", null);
        user.setGameChangeNotificationsEnabled(false);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/send/additional")
                        .content(objectMapper.writeValueAsString(telegramApiMessage))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(tgBot, Mockito.times(0)).sendAttachmentMessageToChannel(any(), eq(telegramApiMessage.attachmentURL()), eq(telegramApiMessage.message()));
    }
}