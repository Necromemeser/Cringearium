document.addEventListener('DOMContentLoaded', function() {
    // Получаем ID курса из URL
    const pathParts = window.location.pathname.split('/');
    const courseId = pathParts[pathParts.length - 1];

    // Элементы DOM
    const courseNameElement = document.getElementById('courseName');
    const courseThemeElement = document.getElementById('courseTheme');
    const createdAtElement = document.getElementById('createdAt');
    const priceElement = document.getElementById('price');
    const courseDescriptionElement = document.getElementById('courseDescription');
    const courseImageElement = document.getElementById('courseImage');
    const usersCountElement = document.getElementById('usersCount');
    const enrollBtn = document.getElementById('enrollBtn');
    const paymentModal = new bootstrap.Modal(document.getElementById('paymentModal'));
    const modalCourseName = document.getElementById('modalCourseName');
    const modalCoursePrice = document.getElementById('modalCoursePrice');
    const proceedToPayment = document.getElementById('proceedToPayment');

    // Загружаем данные курса
    fetch(`/api/courses/${courseId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Курс не найден');
            }
            return response.json();
        })
        .then(course => {
            // Заполняем данные на странице
            courseNameElement.textContent = course.courseName;
            courseThemeElement.textContent = course.courseTheme;
            createdAtElement.textContent = new Date(course.createdAt).toLocaleDateString();
            priceElement.textContent = `${course.price} ₽`;
            courseDescriptionElement.textContent = course.courseDescription;

            // Загрузка изображения
            courseImageElement.src = course.courseImage
                ? `/api/courses/${course.id}/image`
                : `/images/default-course.jpg`;
            usersCountElement.textContent = course.users ? course.users.length : 0;

            // Настраиваем модальное окно
            modalCourseName.textContent = course.courseName;
            modalCoursePrice.textContent = `${course.price} ₽`;

        })
        .catch(error => {
            console.error('Ошибка загрузки курса:', error);
            // Можно добавить обработку ошибки, например, редирект на 404
        });

    // Обработчик кнопки "Записаться на курс"
    enrollBtn.addEventListener('click', function() {
        paymentModal.show();
    });

    // Обработчик кнопки "Войдите, чтобы оплатить" (если она существует)
    const LogInToPayBtn = document.getElementById('LogInToPayBtn');
    if (LogInToPayBtn) {
        LogInToPayBtn.addEventListener('click', function() {
            window.location.href = '/login';
        });
    }

    // Обработчик кнопки "Оплатить курс"
    const proceedToPaymentBtn = document.getElementById('proceedToPaymentBtn');
    if (proceedToPaymentBtn) {
        proceedToPaymentBtn.addEventListener('click', function() {

            // Получаем ID курса из URL
            const pathParts = window.location.pathname.split('/');
            const courseId = pathParts[pathParts.length - 1];

            // Показываем индикатор загрузки
            const originalText = enrollBtn.innerHTML;
            proceedToPaymentBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Обработка...';
            proceedToPaymentBtn.disabled = true;

            // Отправляем запрос на создание заказа
            $.ajax({
                url: '/api/orders',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    status: "pending",
                    course_id: courseId,
                    user_id: null
                }),
                success: function(response) {
                    // Обработка успешного создания заказа
                    console.log('Order created:', response);

                    //Перенаправление на страницу оплаты
                    window.location.href = response.paymentUrl;

                },
                error: function(xhr, status, error) {
                    console.error('Ошибка при создании заказа:', {
                        status: status,
                        error: error,
                        response: xhr.responseText
                    });

                    // Восстанавливаем кнопку
                    enrollBtn.innerHTML = originalText;
                    enrollBtn.disabled = false;

                    // Показываем ошибку пользователю
                    const errorMessage = xhr.responseJSON && xhr.responseJSON.message
                        ? xhr.responseJSON.message
                        : 'Произошла ошибка при создании заказа';
                    alert(errorMessage);
                }
            });
        });
    }

});