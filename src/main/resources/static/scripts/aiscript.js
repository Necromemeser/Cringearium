$(document).ready(function() {
    // Функция для создания сообщения от ИИ
    function createAiMessage(content, isOldMessage = false) {
        const aiMessageEl = $('<div class="message ai-message"><strong>Кринжик:</strong> </div>');
        const mainTextEl = $('<span class="ai-text"></span>');

        mainTextEl.text(content);  // безопасное добавление текста
        aiMessageEl.append(mainTextEl);

        return aiMessageEl;
    }


    // Получение списка чатов
    function fetchChats() {
        $.ajax({
            url: '/api/chats',
            method: 'GET',
            dataType: 'json',
            success: function(chats) {
                $('#chatList').empty();
                if (Array.isArray(chats)) {
                    chats.forEach((chat) => {
                        $('#chatList').append(`
                            <li class="chatItem" data-id="${chat.id}">
                                ${chat.chatName} <button class="deleteChatBtn">Удалить</button>
                            </li>
                        `);
                    });
                } else {
                    console.error('Unexpected response format:', chats);
                    alert('Ошибка формата данных');
                }
            },
            error: function(xhr, status, error) {
                console.error('Ошибка при загрузке чатов:', {
                    status: status,
                    error: error,
                    response: xhr.responseText
                });
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
                console.log('Чат создан:', chat);
                fetchChats();
                $(`.chatItem[data-id="${chat.id}"]`).click();
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

    // Удаление чата
    $(document).on('click', '.deleteChatBtn', function(event) {
        event.stopPropagation();
        const chatId = $(this).parent().data('id');
        $.ajax({
            url: `/api/chats/${chatId}`,
            method: 'DELETE',
            success: function() {
                fetchChats();
                $('#response').empty();
            },
            error: function(xhr, status, error) {
                console.error('Ошибка при удалении чата:', {
                    status: status,
                    error: error,
                    response: xhr.responseText
                });
                alert('Ошибка при удалении чата ' + chatId);
            }
        });
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

    // Отправка новых сообщений
    $('#chatForm').on('submit', function(event) {
        event.preventDefault();
        const userInput = $('#userInput').val();
        const activeChatId = $('.chatItem.selected').data('id');

        if (!activeChatId) {
            alert('Выберите чат для отправки сообщения');
            return;
        }

        if (!userInput.trim()) {
            return;
        }

        $('#response').append(`<div class="message user-message"><strong>Вы:</strong> ${userInput}</div>`);
        $('#userInput').val('');

        const loadingEl = $('<div class="message loading">Кринжик печатает...</div>');
        $('#response').append(loadingEl);

        // Создаём блок для AI-сообщения
        const aiMessageEl = $('<div class="message ai-message"><strong>Кринжик:</strong> <span class="ai-text"></span></div>');
        const aiTextEl = aiMessageEl.find('.ai-text');
        $('#response').append(aiMessageEl);

        fetch(`/api/deepseek?chatId=${activeChatId}&input=${encodeURIComponent(userInput)}`, {
            method: 'POST'
        }).then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text || 'Ошибка сети');
                });
            }

            const reader = response.body.getReader();
            const decoder = new TextDecoder();

            function readStream() {
                reader.read().then(({ done, value }) => {
                    if (done) {
                        loadingEl.remove();
                        return;
                    }

                    const textChunk = decoder.decode(value, { stream: true });
                    aiTextEl.append(document.createTextNode(textChunk));  // безопасное добавление текста
                    readStream();
                });
            }

            readStream();
        }).catch(error => {
            console.error('Ошибка при отправке сообщения:', error);
            loadingEl.remove();
            $('#response').append(`<div class="message ai-message"><strong>Кринжик:</strong> Ошибка: ${error.message}</div>`);
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
