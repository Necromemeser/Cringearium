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
                : `/images/default-course.png`;

            const card = document.createElement("div");
            card.className = "col-md-6 col-lg-4";
            card.innerHTML = `
                <div class="card h-100 shadow-sm">
                    <img src="${imageSrc}" class="card-img-top" alt="Изображение курса">
                    <div class="card-body">
                        <h5 class="card-title">${course.courseName}</h5>
                        <p class="card-text text-muted">${course.courseTheme || 'Описание отсутствует'}</p>
                    </div>
                    <div class="card-footer bg-transparent d-flex justify-content-between">
                        <a href="/courses/${course.id}/study" class="btn btn-sm btn-outline-primary">Продолжить</a>
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