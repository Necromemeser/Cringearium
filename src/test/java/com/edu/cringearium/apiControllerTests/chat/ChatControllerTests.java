package com.edu.cringearium.apiControllerTests.chat;


import com.edu.cringearium.config.security.CustomUserDetails;
import com.edu.cringearium.controllers.api.chat.ChatController;
import com.edu.cringearium.dto.ChatDTO;
import com.edu.cringearium.entities.chat.Chat;
import com.edu.cringearium.entities.user.User;
import com.edu.cringearium.repositories.chat.ChatRepository;
import com.edu.cringearium.services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTests {

    private MockMvc mockMvc;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ChatController chatController;

    private User currentUser;
    private Chat chat;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();

        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("testUser");

        chat = new Chat();
        chat.setId(1L);
        chat.setChatName("Test Chat");
        chat.setCreatedAt(LocalDateTime.now());
        chat.setParticipants(Set.of(currentUser));

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUser()).thenReturn(currentUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAllChats_ShouldReturnListOfChats() throws Exception {
        when(chatRepository.findByParticipantsContaining(currentUser)).thenReturn(List.of(chat));

        mockMvc.perform(get("/api/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(chat.getId()))
                .andExpect(jsonPath("$[0].chatName").value(chat.getChatName()));
    }

    @Test
    void getChatById_ShouldReturnChat_WhenUserIsParticipant() throws Exception {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        mockMvc.perform(get("/api/chats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(chat.getId()))
                .andExpect(jsonPath("$.chatName").value(chat.getChatName()));
    }

    @Test
    void getChatById_ShouldReturnNotFound_WhenChatDoesNotExist() throws Exception {
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/chats/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createChat_ShouldSaveAndReturnChat() throws Exception {
        Chat newChat = new Chat();
        newChat.setChatName("New Chat");
        newChat.setParticipants(Set.of(currentUser));

        when(chatRepository.save(any(Chat.class))).thenReturn(chat);

        mockMvc.perform(post("/api/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chatName\":\"New Chat\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(chat.getId()))
                .andExpect(jsonPath("$.chatName").value(chat.getChatName()));
    }

    @Test
    void deleteChat_ShouldDeleteChat_WhenUserIsParticipant() throws Exception {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        doNothing().when(chatRepository).deleteById(1L);

        mockMvc.perform(delete("/api/chats/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteChat_ShouldReturnNotFound_WhenChatDoesNotExist() throws Exception {
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/chats/1"))
                .andExpect(status().isNotFound());
    }
}
