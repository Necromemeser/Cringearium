$(document).ready(function() {
    const apiUrl = '/api/user';
    let usersTable;
    let currentUserId = null;

    // Контейнер для уведомлений
    $('body').append('<div id="alertContainer" class="position-fixed bottom-0 end-0 p-3" style="z-index: 1050;"></div>');

    function showToast(title, message, type) {
        const alertId = `alert-${Date.now()}`;
        const alertHtml = `
            <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show" role="alert">
                <strong>${title}</strong> ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        $('#alertContainer').append(alertHtml);
        setTimeout(() => {
            $(`#${alertId}`).alert('close');
        }, 5000);
    }

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
                        if (data === 'Admin') badgeClass = 'bg-primary';
                        if (data === 'Ban') badgeClass = 'bg-danger';
                        if (data === 'Student') badgeClass = 'bg-success';
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
                                <button class="btn btn-sm btn-outline-danger ban-btn" data-id="${data}">
                                    <i class="fas fa-ban"></i>
                                </button>
                            </div>
                        `;
                    },
                    orderable: false
                }
            ]
        });
    }

    $(document).on('click', '.edit-btn', function() {
        currentUserId = $(this).data('id');
        $.get(`${apiUrl}/${currentUserId}`, function(user) {
            $('#modalTitle').text('Редактирование пользователя');
            $('#userId').val(user.id);
            $('#username').val(user.username);
            $('#email').val(user.email);
            $('#userRole').val(user.userRole);
            $('#userModal').modal('show');
        }).fail(function() {
            showToast('Ошибка', 'Не удалось загрузить данные пользователя', 'danger');
        });
    });

    $(document).on('click', '#addUserBtn', function() {
        currentUserId = null;
        $('#modalTitle').text('Добавление пользователя');
        $('#userForm')[0].reset();
        $('#userModal').modal('show');
    });

    $(document).on('click', '.ban-btn', function() {
        currentUserId = $(this).data('id');
        $('#confirmBanModal').modal('show');
    });

    $('#saveUserBtn').click(function() {
        const userData = {
            id: currentUserId,
            username: $('#username').val(),
            email: $('#email').val(),
            user_role_id: Number($('#userRole').val())
        };

        const password = $('#password').val();
        if (password) {
            userData.passwordHash = password;
        }

        const url = currentUserId ? `${apiUrl}/${currentUserId}` : apiUrl;
        const method = currentUserId ? 'PUT' : 'POST';


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

    $('#confirmBanBtn').click(function() {
        $.ajax({
            url: `${apiUrl}/${currentUserId}/ban`,
            type: 'PUT',
            success: function() {
                usersTable.ajax.reload();
                $('#confirmBanModal').modal('hide');
                showToast('Успех', 'Пользователь забанен', 'success');
            },
            error: function(xhr) {
                showToast('Ошибка', xhr.responseJSON?.message || 'Произошла ошибка', 'danger');
            }
        });
    });

    initUsersTable();
});