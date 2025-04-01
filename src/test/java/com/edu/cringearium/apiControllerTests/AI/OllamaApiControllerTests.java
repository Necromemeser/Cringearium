// ======= Пофиксить! ========


//package com.edu.cringearium.apiControllerTests.AI;
//
//import com.edu.cringearium.controllers.api.AI.OllamaApiController;
//import com.edu.cringearium.services.MessageService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.client.advisor.AbstractPromptVisitor;
//import org.springframework.ai.chat.client.advisor.PromptAdvisor;
//import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
//import org.springframework.ai.chat.client.request.ChatClientRequest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import reactor.core.publisher.Flux;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//

//@ExtendWith(MockitoExtension.class)
//class OllamaApiControllerTests {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private ChatClient chatClient;
//
//    @Mock
//    private ChatClient.ChatClientRequest promptRequest;
//
//    @Mock
//    private ChatClient.ChatClientRequest.ChatClientRequestSpec requestSpec;
//
//    @Mock
//    private MessageService messageService;
//
//    @InjectMocks
//    private OllamaApiController ollamaApiController;
//
//    private final Long TEST_CHAT_ID = 1L;
//    private final String TEST_INPUT = "Hello, AI!";
//    private final String TEST_RESPONSE = "Hello, human!";
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(ollamaApiController).build();
//
//        // Настройка моков для ChatClient
//        when(chatClient.prompt()).thenReturn(promptRequest);
//        when(promptRequest.user(anyString())).thenReturn(requestSpec);
//        when(requestSpec.stream()).thenReturn(requestSpec);
//        when(requestSpec.content()).thenReturn(Flux.just("Response ", "from AI"));
//    }
//
//    @Test
//    void postOllama_ShouldReturnStreamAndSaveMessages() throws Exception {
//        // Выполнение запроса и проверка
//        mockMvc.perform(post("/api/ollama")
//                        .param("chatId", TEST_CHAT_ID.toString())
//                        .param("input", TEST_INPUT))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Response from AI"));
//
//        // Проверка вызовов сервисов
//        verify(messageService).saveUserMessage(TEST_CHAT_ID, TEST_INPUT);
//        verify(messageService).saveAiResponse(eq(TEST_CHAT_ID), anyString());
//    }
//
//    @Test
//    void getOllama_ShouldReturnStream() throws Exception {
//        // Выполнение запроса и проверка
//        mockMvc.perform(get("/api/ollama")
//                        .param("input", TEST_INPUT))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Response from AI"));
//
//        // Проверка, что сервис сообщений не вызывался
//        verifyNoInteractions(messageService);
//    }
//
//    @Test
//    void postOllama_ShouldHandleMissingParameters() throws Exception {
//        // Тест без chatId
//        mockMvc.perform(post("/api/ollama")
//                        .param("input", TEST_INPUT))
//                .andExpect(status().isBadRequest());
//
//        // Тест без input
//        mockMvc.perform(post("/api/ollama")
//                        .param("chatId", TEST_CHAT_ID.toString()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void postOllama_ShouldHandleEmptyInput() throws Exception {
//        mockMvc.perform(post("/api/ollama")
//                        .param("chatId", TEST_CHAT_ID.toString())
//                        .param("input", ""))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void postOllama_ShouldContinueOnSaveError() throws Exception {
//        // Настройка моков для ошибки сохранения
//        doThrow(new RuntimeException("DB error"))
//                .when(messageService)
//                .saveUserMessage(anyLong(), anyString());
//
//        // Запрос должен завершиться успешно несмотря на ошибку
//        mockMvc.perform(post("/api/ollama")
//                        .param("chatId", TEST_CHAT_ID.toString())
//                        .param("input", TEST_INPUT))
//                .andExpect(status().isOk());
//    }
//}