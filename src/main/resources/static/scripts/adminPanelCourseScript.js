$(document).ready(function() {
    const apiUrl = '/api/courses';
    let coursesTable;
    let currentCourseId = null;

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

    $(document).on('click', '.edit-course-btn', function() {
        currentCourseId = $(this).data('id');
        $.get(`${apiUrl}/${currentCourseId}`, function(course) {
            $('#courseModalTitle').text('Редактирование курса');
            $('#courseId').val(course.id);
            $('#courseName').val(course.courseName);
            $('#courseTheme').val(course.courseTheme);
            $('#coursePrice').val(course.price);
            $('#courseDescription').val(course.courseDescription);

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

    $(document).on('click', '#addCourseBtn', function() {
        currentCourseId = null;
        $('#courseModalTitle').text('Добавление курса');
        $('#courseForm')[0].reset();
        $('#courseImagePreview').hide();
        $('#courseModal').modal('show');
    });

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

    $(document).on('click', '.delete-course-btn', function() {
        currentCourseId = $(this).data('id');
        $('#confirmDeleteCourseModal').modal('show');
    });

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

    initCoursesTable();
});