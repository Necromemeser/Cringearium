$(document).ready(function() {
    // Функция для создания AI-сообщения с поддержкой think-блоков
    function createAiMessage(content, isOldMessage = false) {
        const aiMessageEl = $('<div class="message ai-message"><strong>Кринжик:</strong> </div>');
        const mainTextEl = $('<div class="ai-main-text"></div>');
        const thinkContainer = $('<div class="think-container"></div>');
        const thinkToggle = $('<button class="think-toggle">📊 Показать рассуждения</button>');
        const thinkContent = $('<div class="think-content" style="display:none"></div>');

        let inThinkBlock = false;
        let remainingContent = content;
        let hasThinkContent = false;

        // Парсим контент для выделения think-блоков
        while (remainingContent.length > 0) {
            if (!inThinkBlock) {
                const thinkStart = remainingContent.indexOf('<think>');
                if (thinkStart >= 0) {
                    mainTextEl.append(remainingContent.substring(0, thinkStart));
                    inThinkBlock = true;
                    remainingContent = remainingContent.substring(thinkStart + 7);
                    hasThinkContent = true;
                } else {
                    mainTextEl.append(remainingContent);
                    remainingContent = '';
                }
            } else {
                const thinkEnd = remainingContent.indexOf('</think>');
                if (thinkEnd >= 0) {
                    thinkContent.append(remainingContent.substring(0, thinkEnd));
                    remainingContent = remainingContent.substring(thinkEnd + 8);
                    inThinkBlock = false;
                } else {
                    thinkContent.append(remainingContent);
                    remainingContent = '';
                }
            }
        }

        // Собираем структуру сообщения
        thinkContainer.append(thinkToggle).append(thinkContent);
        aiMessageEl.append(thinkContainer).append(mainTextEl);

        // Настраиваем поведение кнопки, если есть think-контент
        if (hasThinkContent) {
            thinkToggle.on('click', function() {
                thinkContent.slideToggle();
                $(this).text(
                    thinkContent.is(':visible')
                    ? '📊 Скрыть рассуждения'
                    : '📊 Показать рассуждения'
                );
            });
        } else {
            thinkContainer.remove();
        }

        // Для старых сообщений сразу показываем основной текст
        if (isOldMessage) {
            mainTextEl.show();
        }

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

        // Создаем элементы для вывода
        const aiMessageEl = $('<div class="message ai-message"><strong>Кринжик:</strong> </div>');
        const mainTextEl = $('<div class="ai-main-text"></div>');
        const thinkContainer = $('<div class="think-container"></div>');
        const thinkToggle = $('<button class="think-toggle">📊 Скрыть рассуждения</button>');
        const thinkContent = $('<div class="think-content"></div>');

        // Собираем структуру (рассуждения перед основным ответом)
        thinkContainer.append(thinkToggle).append(thinkContent);
        aiMessageEl.append(thinkContainer).append(mainTextEl);
        $('#response').append(aiMessageEl);

        let inThinkBlock = false;
        let thinkContentVisible = false;  // По умолчанию не видим рассуждения

        // Обработчик для кнопки (добавляем сразу)
        thinkToggle.on('click', function() {
            thinkContentVisible = !thinkContentVisible;
            thinkContent.toggle(thinkContentVisible);
            $(this).text(
                thinkContentVisible
                ? '📊 Скрыть рассуждения'
                : '📊 Показать рассуждения'
            );
        });

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

            function processChunk(text) {
                let remainingText = text;

                while (remainingText.length > 0) {
                    if (!inThinkBlock) {
                        const thinkStart = remainingText.indexOf('<think>');
                        if (thinkStart >= 0) {
                            // Добавляем текст до тега <think>
                            mainTextEl.append(remainingText.substring(0, thinkStart));
                            inThinkBlock = true;
                            remainingText = remainingText.substring(thinkStart + 7);
                        } else {
                            // Просто добавляем весь текст
                            mainTextEl.append(remainingText);
                            remainingText = '';
                        }
                    } else {
                        const thinkEnd = remainingText.indexOf('</think>');
                        if (thinkEnd >= 0) {
                            // Добавляем содержимое think-блока
                            thinkContent.append(remainingText.substring(0, thinkEnd));
                            remainingText = remainingText.substring(thinkEnd + 8);
                            inThinkBlock = false;
                        } else {
                            // Добавляем весь текст в think-блок
                            thinkContent.append(remainingText);
                            remainingText = '';
                        }
                    }
                }
            }

            function readStream() {
                reader.read().then(({ done, value }) => {
                    if (done) {
                        loadingEl.remove();
                        return;
                    }

                    const textChunk = decoder.decode(value, { stream: true });
                    processChunk(textChunk);
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
