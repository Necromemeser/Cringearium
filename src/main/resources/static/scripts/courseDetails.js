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
//            courseImageElement.src = course.courseImage || 'https://via.placeholder.com/800x400';
            // Загрузка изображения
            courseImageElement.src = course.courseImage
                ? `/api/courses/${course.id}/image`
                : `/images/default-course.jpg`;
            usersCountElement.textContent = course.users ? course.users.length : 0;

            // Настраиваем модальное окно
            modalCourseName.textContent = course.courseName;
            modalCoursePrice.textContent = `${course.price} ₽`;
            proceedToPayment.href = `/payment/${courseId}`;
        })
        .catch(error => {
            console.error('Ошибка загрузки курса:', error);
            // Можно добавить обработку ошибки, например, редирект на 404
        });

    // Обработчик кнопки "Записаться на курс"
    enrollBtn.addEventListener('click', function() {
        paymentModal.show();
    });
});