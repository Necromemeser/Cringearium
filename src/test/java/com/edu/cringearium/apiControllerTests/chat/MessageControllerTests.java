package com.edu.cringearium.apiControllerTests.chat;

import com.edu.cringearium.entities.chat.Chat;
import com.edu.cringearium.entities.chat.Message;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.repositories.chat.ChatRepository;
import com.edu.cringearium.repositories.chat.MessageRepository;
import com.edu.cringearium.repositories.user.UserRepository;
import com.edu.cringearium.controllers.api.chat.MessageController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTests {

    private MockMvc mockMvc;

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    void getMessagesByChat_ShouldReturnMessages() throws Exception {
        Long chatId = 1L;
        Message message = new Message("Hello".getBytes(), null, new Chat(), false);
        when(messageRepository.findByChatId(chatId)).thenReturn(List.of(message));

        mockMvc.perform(get("/api/messages/chat/{chatId}", chatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").exists());
    }

    @Test
    void sendMessage_ShouldSaveAndReturnMessage() throws Exception {
        Long userId = 1L;
        Long chatId = 2L;
        User user = new User();
        Chat chat = new Chat();
        Message message = new Message("Hello".getBytes(), user, chat, false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        mockMvc.perform(post("/api/messages/send")
                        .param("userId", userId.toString())
                        .param("chatId", chatId.toString())
                        .content("Hello".getBytes())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }


    @Test
    void sendMessage_ShouldReturnBadRequest_WhenUserIdIsMissingAndNotAiResponse() throws Exception {
        Long chatId = 2L;

        mockMvc.perform(post("/api/messages/send")
                        .param("chatId", chatId.toString())
                        .param("isAiResponse", "false") // Сообщение не от ИИ, но userId отсутствует
                        .content("Hello".getBytes())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isBadRequest());
    }




    @Test
    void sendMessage_ShouldAllowAiMessageWithoutUserId() throws Exception {
        Long chatId = 2L;
        Chat chat = new Chat();
        Message message = new Message("AI Response".getBytes(), null, chat, true);

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        mockMvc.perform(post("/api/messages/send")
                        .param("chatId", chatId.toString())
                        .param("isAiResponse", "true")
                        .content("AI Response".getBytes())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }
}

