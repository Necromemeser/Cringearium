$(document).ready(function() {
    const apiUrl = '/api/courses';
    let coursesTable;
    let currentCourseId = null;

    // Инициализация DataTable для курсов
    function initCoursesTable() {
        coursesTable = $('#coursesTable').DataTable({
            ajax: {
                url: apiUrl,
                dataSrc: ''
            },
            columns: [
                { data: 'id' },
                {
                    data: 'courseImage',
                    render: function(data) {
                        if (data) {
                            return `<img src="data:image/jpeg;base64,${data}" class="course-img-thumb">`;
                        }
                        return `<img src="/images/default-course.png" class="course-img-thumb">`;
                    }
                },
                { data: 'courseName' },
                { data: 'courseTheme' },
                {
                    data: 'price',
                    render: function(data) {
                        return data ? `${data} ₽` : 'Бесплатно';
                    }
                },
                {
                    data: 'createdAt',
                    render: function(data) {
                        return new Date(data).toLocaleDateString('ru-RU');
                    }
                },
                {
                    data: 'id',
                    render: function(data) {
                        return `
                            <div class="action-buttons">
                                <button class="btn btn-sm btn-outline-primary edit-course-btn" data-id="${data}">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-sm btn-outline-danger delete-course-btn" data-id="${data}">
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
    $(document).on('click', '.edit-course-btn', function() {
        currentCourseId = $(this).data('id');
        $.get(`${apiUrl}/${currentCourseId}`, function(course) {
            $('#courseModalTitle').text('Редактирование курса');
            $('#courseId').val(course.id);
            $('#courseName').val(course.courseName);
            $('#courseTheme').val(course.courseTheme);
            $('#coursePrice').val(course.price);
            $('#courseDescription').val(course.courseDescription);

            // Превью изображения
            if (course.courseImage) {
                $('#courseImagePreview').attr('src', `data:image/jpeg;base64,${course.courseImage}`).show();
            } else {
                $('#courseImagePreview').hide();
            }

            $('#courseModal').modal('show');
        }).fail(function() {
            showToast('Ошибка', 'Не удалось загрузить данные курса', 'danger');
        });
    });

    // Открытие модального окна для добавления
    $(document).on('click', '#addCourseBtn', function() {
        currentCourseId = null;
        $('#courseModalTitle').text('Добавление курса');
        $('#courseForm')[0].reset();
        $('#courseImagePreview').hide();
        $('#courseModal').modal('show');
    });

    // Просмотр изображения перед загрузкой
    $('#courseImage').change(function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $('#courseImagePreview').attr('src', e.target.result).show();
            }
            reader.readAsDataURL(file);
        }
    });

    // Подтверждение удаления
    $(document).on('click', '.delete-course-btn', function() {
        currentCourseId = $(this).data('id');
        $('#confirmDeleteCourseModal').modal('show');
    });

    // Сохранение курса
    $('#saveCourseBtn').click(function() {
        const formData = new FormData();
        formData.append('courseName', $('#courseName').val());
        formData.append('courseTheme', $('#courseTheme').val());
        formData.append('price', $('#coursePrice').val());
        formData.append('courseDescription', $('#courseDescription').val());

        const courseImage = $('#courseImage')[0].files[0];
        if (courseImage) {
            formData.append('courseImage', courseImage);
        }

        const url = currentCourseId ? `${apiUrl}/${currentCourseId}` : apiUrl;
        const method = currentCourseId ? 'PUT' : 'POST';

        $.ajax({
            url: url,
            type: method,
            data: formData,
            processData: false,
            contentType: false,
            success: function() {
                coursesTable.ajax.reload();
                $('#courseModal').modal('hide');
                showToast('Успех', currentCourseId ? 'Курс обновлен' : 'Курс добавлен', 'success');
            },
            error: function(xhr) {
                showToast('Ошибка', xhr.responseJSON?.message || 'Произошла ошибка', 'danger');
            }
        });
    });

    // Удаление курса
    $('#confirmDeleteCourseBtn').click(function() {
        $.ajax({
            url: `${apiUrl}/${currentCourseId}`,
            type: 'DELETE',
            success: function() {
                coursesTable.ajax.reload();
                $('#confirmDeleteCourseModal').modal('hide');
                showToast('Успех', 'Курс удален', 'success');
            },
            error: function(xhr) {
                showToast('Ошибка', xhr.responseJSON?.message || 'Произошла ошибка', 'danger');
            }
        });
    });

    // Вспомогательная функция для показа уведомлений
    function showToast(title, message, type) {
        // Реализация toast-уведомлений (можно использовать Bootstrap Toasts)
        console.log(`${title}: ${message}`);
        alert(`${title}: ${message}`);
    }

    // Инициализация таблицы
    initCoursesTable();
});