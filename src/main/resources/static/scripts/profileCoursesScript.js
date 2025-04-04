document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/user/courses', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка загрузки курсов');
        return response.json();
    })
    .then(courses => {
        const container = document.getElementById("coursesContainer");
        const placeholder = document.getElementById("noCoursesPlaceholder");

        if (courses.length === 0) {
            placeholder.classList.remove("d-none"); // Показать заглушку
            return;
        }

        console.log("Полученные курсы:", courses);


        courses.forEach(course => {

            const imageSrc = course.hasImage
                ? `/api/courses/${course.id}/image`
                : `/images/default-course.jpg`;

            const card = document.createElement("div");
            card.className = "col-md-6 col-lg-4";
            card.innerHTML = `
                <div class="card h-100 shadow-sm">
                    <img src="${imageSrc}" class="card-img-top" alt="Изображение курса">
                    <div class="card-body">
                        <h5 class="card-title">${course.courseName}</h5>
                        <p class="card-text text-muted">${course.courseTheme || 'Описание отсутствует'}</p>
                        <div class="mb-3">
                            <small class="text-muted">Прогресс:</small>
                            <div class="progress course-progress">
                                <div class="progress-bar bg-success"
                                     role="progressbar"
                                     style="width: ${course.progress || 0}%;"
                                     aria-valuenow="${course.progress || 0}"
                                     aria-valuemin="0"
                                     aria-valuemax="100">
                                </div>
                            </div>
                            <small class="text-muted">${course.progress ? course.progress + '% завершено' : 'Прогресс не отслеживается'}</small>
                        </div>
                    </div>
                    <div class="card-footer bg-transparent d-flex justify-content-between">
                        <a href="/courses/${course.id}" class="btn btn-sm btn-outline-primary">Продолжить</a>
                        <button class="btn btn-sm btn-outline-danger">Отписаться</button>
                    </div>
                </div>
            `;
            container.appendChild(card);
        });
    })
    .catch(error => {
        console.error("Ошибка загрузки курсов:", error);
    });
});