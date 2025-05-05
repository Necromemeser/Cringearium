$(document).ready(function() {
    const apiUrl = '/api/user';
    let usersTable;
    let currentUserId = null;

    // Инициализация DataTable
    function initUsersTable() {
        usersTable = $('#usersTable').DataTable({
            ajax: {
                url: apiUrl,
                dataSrc: ''
            },
            columns: [
                { data: 'id' },
                {
                    data: 'profilePic',
                    render: function(data) {
                        if (data) {
                            return `<img src="data:image/jpeg;base64,${data}" class="user-avatar">`;
                        }
                        return `<img src="/images/default-avatar.png" class="user-avatar">`;
                    }
                },
                { data: 'username' },
                { data: 'email' },
                {
                    data: 'userRole',
                    render: function(data) {
                        if (!data) return '';
                        let badgeClass = 'bg-secondary';
                        if (data === 'Admin') badgeClass = 'bg-danger';
                        if (data === 'Curator') badgeClass = 'bg-primary';
                        if (data === 'Teacher') badgeClass = 'bg-success';
                        return `<span class="badge ${badgeClass} badge-role">${data}</span>`;
                    }
                },
                {
                    data: 'courses',
                    render: function(data) {
                        if (!data || data.length === 0) return 'Нет курсов';
                        return data.map(course =>
                            `<span class="badge bg-info text-dark course-badge">${course}</span>`
                        ).join('');
                    }
                },
                {
                    data: 'id',
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-outline-primary edit-btn" data-id="${data}">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-sm btn-outline-danger delete-btn" data-id="${data}">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        `;
                    },
                    orderable: false
                }
            ],
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ru.json'
            }
        });
    }

    // Открытие модального окна для редактирования
    $(document).on('click', '.edit-btn', function() {
        currentUserId = $(this).data('id');
        $.get(`${apiUrl}/${currentUserId}`, function(user) {
            $('#modalTitle').text('Редактирование пользователя');
            $('#userId').val(user.id);
            $('#username').val(user.username);
            $('#email').val(user.email);
            $('#userRole').val(user.userRole);
            $('#userModal').modal('show');
        });
    });

    // Открытие модального окна для добавления
    $(document).on('click', '#addUserBtn', function() {
        currentUserId = null;
        $('#modalTitle').text('Добавление пользователя');
        $('#userForm')[0].reset();
        $('#userModal').modal('show');
    });

    // Подтверждение удаления
    $(document).on('click', '.delete-btn', function() {
        currentUserId = $(this).data('id');
        $('#confirmDeleteModal').modal('show');
    });

    // Сохранение пользователя
    $('#saveUserBtn').click(function() {
        const userData = {
            id: currentUserId,
            username: $('#username').val(),
            email: $('#email').val(),
            user_role_id: Number($('#userRole').val())
        };

        // Добавляем пароль только если он указан
        const password = $('#password').val();
        if (password) {
            userData.passwordHash = password;
        }

        const url = currentUserId ? `${apiUrl}/${currentUserId}` : apiUrl;
        const method = currentUserId ? 'PUT' : 'POST';

        console.log(url, method, userData);

        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(userData),
            success: function() {
                usersTable.ajax.reload();
                $('#userModal').modal('hide');
                showToast('Успех', currentUserId ? 'Пользователь обновлен' : 'Пользователь добавлен', 'success');
            },
            error: function(xhr) {
                showToast('Ошибка', xhr.responseJSON?.message || 'Произошла ошибка', 'danger');
            }
        });
    });

    // Удаление пользователя
    $('#confirmDeleteBtn').click(function() {
        $.ajax({
            url: `${apiUrl}/${currentUserId}`,
            type: 'DELETE',
            success: function() {
                usersTable.ajax.reload();
                $('#confirmDeleteModal').modal('hide');
                showToast('Успех', 'Пользователь удален', 'success');
            },
            error: function(xhr) {
                showToast('Ошибка', xhr.responseJSON?.message || 'Произошла ошибка', 'danger');
            }
        });
    });

    // Вспомогательная функция для показа уведомлений
    function showToast(title, message, type) {
        // Здесь можно реализовать toast-уведомления
        alert(`${title}: ${message}`);
    }

    // Инициализация таблицы
    initUsersTable();
});