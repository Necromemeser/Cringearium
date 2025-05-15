$(document).ready(function() {
    // Функция для создания сообщения от ИИ
    function createAiMessage(content, isOldMessage = false) {
        const aiMessageEl = $('<div class="message ai-message"><strong>Кринжик:</strong> </div>');
        const mainTextEl = $('<span class="ai-text"></span>');

        mainTextEl.text(content);  // безопасное добавление текста
        aiMessageEl.append(mainTextEl);

        return aiMessageEl;
    }


    // Получение списка чатов (обновленная версия)
    function fetchChats() {
        $.ajax({
            url: '/api/chats',
            method: 'GET',
            dataType: 'json',
            success: function(chats) {
                $('#chatList').empty();
                if (Array.isArray(chats)) {
                    // Сортируем чаты по ID в обратном порядке (новые сверху)
                    chats.sort((a, b) => b.id - a.id);

                    chats.forEach((chat) => {
                        $('#chatList').append(`
                            <li class="chatItem" data-id="${chat.id}">
                                Чат ${chat.id}
                                <button class="deleteChatBtn">
                                    <i class="fas fa-trash-alt"></i>
                                </button>
                            </li>
                        `);
                    });
                } else {
                    console.error('Unexpected response format:', chats);
                    alert('Ошибка формата данных');
                }
            },
            error: function(xhr, status, error) {
                console.error('Ошибка при загрузке чатов:', error);
                alert('Ошибка при загрузке чатов');
            }
        });
    }

    // Открытие/закрытие боковой панели
    $('#sidePanelToggle').on('click', function() {
        $('#sidePanel').toggleClass('open');
    });

    // Создание нового чата
    $('#createChatBtn').on('click', function() {
        $.ajax({
            url: '/api/chats',
            method: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({ chatName: "Новый чат" }),
            success: function(chat) {
                fetchChats();

                setTimeout(() => {
                    $(`.chatItem[data-id="${chat.id}"]`).click();
                }, 100);

            },
            error: function(xhr, status, error) {
                console.error('Ошибка при создании чата:', {
                    status: status,
                    error: error,
                    response: xhr.responseText
                });
                alert('Ошибка при создании чата');
            }
        });
    });

    // Удаление чата с модальным окном подтверждения
    let currentChatIdToDelete = null;

    // Показываем модальное окно при клике на удаление
    $(document).on('click', '.deleteChatBtn', function(event) {
        event.stopPropagation();
        currentChatIdToDelete = $(this).parent().data('id');
        $('#deleteChatModal').show();
    });

    // Обработчик подтверждения удаления
    $('#confirmDeleteBtn').on('click', function() {
        if (currentChatIdToDelete) {
            $.ajax({
                url: `/api/chats/${currentChatIdToDelete}`,
                method: 'DELETE',
                success: function() {
                    fetchChats();
                    $('#response').empty();
                    $('#deleteChatModal').hide();
                },
                error: function(xhr) {
                    console.error('Ошибка при удалении чата:', xhr.responseText);
                    alert('Ошибка при удалении чата');
                    $('#deleteChatModal').hide();
                }
            });
        }
    });

    // Обработчик отмены удаления
    $('#cancelDeleteBtn').on('click', function() {
        $('#deleteChatModal').hide();
        currentChatIdToDelete = null;
    });

    // Переключение между чатами и загрузка сообщений
    $(document).on('click', '.chatItem', function() {
        $('.chatItem').removeClass('selected');
        $(this).addClass('selected');

        const chatId = $(this).data('id');
        $('#response').empty();

        $.ajax({
            url: `/api/chats/${chatId}/messages`,
            method: 'GET',
            dataType: 'json',
            success: function(messages) {
                if (Array.isArray(messages)) {
                    messages.forEach((msg) => {
                        if (msg.isAiResponse) {
                            $('#response').append(createAiMessage(msg.content, true));
                        } else {
                            const sender = msg.userId ? 'Вы' : 'Пользователь';
                            $('#response').append(`<div class="message user-message"><strong>${sender}:</strong> ${msg.content}</div>`);
                        }
                    });
                } else {
                    console.error('Unexpected messages format:', messages);
                    alert('Ошибка формата данных сообщений');
                }
            },
            error: function(xhr, status, error) {
                console.error('Ошибка при загрузке сообщений:', {
                    status: status,
                    error: error,
                    response: xhr.responseText,
                    statusCode: xhr.status
                });

                switch(xhr.status) {
                    case 404:
                        alert('Чат не найден');
                        break;
                    case 403:
                        alert('У вас нет доступа к этому чату');
                        break;
                    case 500:
                        alert('Внутренняя ошибка сервера');
                        break;
                    default:
                        alert('Ошибка при загрузке сообщений');
                }
            }
        });
    });

    // Отправка новых сообщений (исправленная версия)
    $('#chatForm').on('submit', function(event) {
        event.preventDefault();
        const userInput = $('#userInput').val().trim();

        if (!userInput) return;

        // Используем let вместо const для activeChatId
        let activeChatId = $('.chatItem.selected').data('id');

        // Создаем функцию для отправки сообщения
        const sendMessage = function(chatId) {
            $('#response').append(`<div class="message user-message"><strong>Вы:</strong> ${userInput}</div>`);
            $('#userInput').val('');

            const loadingEl = $('<div class="message loading">Кринжик печатает...</div>');
            $('#response').append(loadingEl);

            const aiMessageEl = $('<div class="message ai-message"><strong>Кринжик:</strong> <span class="ai-text"></span></div>');
            const aiTextEl = aiMessageEl.find('.ai-text');
            $('#response').append(aiMessageEl);

            fetch(`/api/deepseek?chatId=${chatId}&input=${encodeURIComponent(userInput)}`, {
                method: 'POST'
            }).then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка сети');
                }
                return response;
            }).then(response => {
                const reader = response.body.getReader();
                const decoder = new TextDecoder();

                function readStream() {
                    reader.read().then(({ done, value }) => {
                        if (done) {
                            loadingEl.remove();
                            return;
                        }
                        const textChunk = decoder.decode(value, { stream: true });
                        aiTextEl.append(document.createTextNode(textChunk));
                        readStream();
                    });
                }
                readStream();
            }).catch(error => {
                console.error('Ошибка:', error);
                loadingEl.remove();
                $('#response').append(`<div class="message ai-message"><strong>Ошибка:</strong> ${error.message}</div>`);
            });
        };

        // Если чат уже выбран - сразу отправляем сообщение
        if (activeChatId) {
            sendMessage(activeChatId);
            return;
        }

        // Если чат не выбран - создаем новый
        $.ajax({
            url: '/api/chats',
            method: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({ chatName: userInput.substring(0, 30) || "Новый чат" })
        }).done(function(chat) {
            // Обновляем список чатов и выбираем новый чат
            fetchChats();

            // Небольшая задержка для обновления DOM
            setTimeout(() => {
                $(`.chatItem[data-id="${chat.id}"]`).addClass('selected');
                sendMessage(chat.id);
            }, 100);
        }).fail(function(xhr) {
            console.error('Ошибка при создании чата:', xhr.responseText);
            $('#response').append(`<div class="message ai-message"><strong>Ошибка:</strong> Не удалось создать чат</div>`);
        });
    });


    // Загружаем чаты при загрузке страницы
    fetchChats();
});

$('#userInput').on('keydown', function(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault(); // предотвращает перенос строки
        $('#chatForm').submit(); // вызывает отправку формы
    }
});
