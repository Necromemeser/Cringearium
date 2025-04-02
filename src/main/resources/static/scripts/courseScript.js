// Текущая страница и параметры фильтрации
let currentPage = 1;
const coursesPerPage = 6;
let totalCourses = 0;
let currentFilters = {
    search: '',
    theme: '',
    price: '',
    sort: 'newest'
};

// Инициализация страницы
function initCoursesPage() {
    loadCourses();

    // Обработчики событий для фильтров
    document.getElementById('searchInput').addEventListener('keyup', function(e) {
        if (e.key === 'Enter') {
            applyFilters();
        }
    });

    document.getElementById('searchButton').addEventListener('click', applyFilters);
    document.getElementById('themeFilter').addEventListener('change', applyFilters);
    document.getElementById('priceFilter').addEventListener('change', applyFilters);
    document.getElementById('sortFilter').addEventListener('change', applyFilters);
    document.getElementById('resetFilters').addEventListener('click', resetFilters);
}

// Применение фильтров
function applyFilters() {
    currentPage = 1;
    currentFilters = {
        search: document.getElementById('searchInput').value,
        theme: document.getElementById('themeFilter').value,
        price: document.getElementById('priceFilter').value,
        sort: document.getElementById('sortFilter').value
    };
    loadCourses();
}

// Сброс фильтров
function resetFilters() {
    document.getElementById('searchInput').value = '';
    document.getElementById('themeFilter').value = '';
    document.getElementById('priceFilter').value = '';
    document.getElementById('sortFilter').value = 'newest';
    applyFilters();
}

// Загрузка курсов с сервера
function loadCourses() {
    const container = document.getElementById('coursesContainer');
    container.innerHTML = `
        <div class="col-12 text-center my-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Загрузка...</span>
            </div>
        </div>
    `;

    // Формируем URL для запроса с учетом фильтров
    let url = '/api/courses';
    const params = [];

    if (currentFilters.search) {
        params.push(`name=${encodeURIComponent(currentFilters.search)}`);
    }
    if (currentFilters.theme) {
        params.push(`theme=${encodeURIComponent(currentFilters.theme)}`);
    }

    if (params.length > 0) {
        url += `?${params.join('&')}`;
    }

    fetch(url)
        .then(response => response.json())
        .then(courses => {
            // Применяем дополнительные фильтры (цена, сортировка)
            let filteredCourses = [...courses];

            // Фильтр по цене
            if (currentFilters.price === 'free') {
                filteredCourses = filteredCourses.filter(course => course.price === 0 || course.price === null);
            } else if (currentFilters.price === 'paid') {
                filteredCourses = filteredCourses.filter(course => course.price > 0);
            }

            // Сортировка
            switch (currentFilters.sort) {
                case 'newest':
                    filteredCourses.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                    break;
                case 'oldest':
                    filteredCourses.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
                    break;
                case 'price_asc':
                    filteredCourses.sort((a, b) => (a.price || 0) - (b.price || 0));
                    break;
                case 'price_desc':
                    filteredCourses.sort((a, b) => (b.price || 0) - (a.price || 0));
                    break;
                case 'name_asc':
                    filteredCourses.sort((a, b) => a.courseName.localeCompare(b.courseName));
                    break;
                case 'name_desc':
                    filteredCourses.sort((a, b) => b.courseName.localeCompare(a.courseName));
                    break;
            }

            totalCourses = filteredCourses.length;
            displayCourses(filteredCourses);
            setupPagination(filteredCourses);
        })
        .catch(error => {
            console.error('Ошибка при загрузке курсов:', error);
            container.innerHTML = `
                <div class="col-12 text-center my-5">
                    <div class="alert alert-danger">Произошла ошибка при загрузке курсов. Пожалуйста, попробуйте позже.</div>
                </div>
            `;
        });
}

// Отображение курсов на странице
function displayCourses(courses) {
    const container = document.getElementById('coursesContainer');

    if (courses.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center my-5">
                <div class="alert alert-info">Курсы по вашему запросу не найдены. Попробуйте изменить параметры поиска.</div>
            </div>
        `;
        return;
    }

    // Вычисляем курсы для текущей страницы
    const startIndex = (currentPage - 1) * coursesPerPage;
    const endIndex = Math.min(startIndex + coursesPerPage, courses.length);
    const paginatedCourses = courses.slice(startIndex, endIndex);



    let html = '';

    paginatedCourses.forEach(course => {
        const priceText = course.price ? `${course.price} ₽` : 'Бесплатно';
        const priceClass = course.price ? 'text-success fw-bold' : 'text-primary fw-bold';

        // Загрузка изображения
        const imageSrc = course.courseImage
            ? `/api/courses/${course.id}/image`
            : `/images/default-course.jpg`;

        console.log(course)
        console.log(imageSrc)

        html += `
            <div class="col-md-6 col-lg-4 mb-4">
                <div class="card h-100 shadow-sm course-card">
                    <img src="${imageSrc}" class="card-img-top" alt="${course.courseName}"
                    onerror="this.onerror=null; this.src='/images/default-course.jpg'">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <span class="badge bg-primary">${course.courseTheme}</span>
                            <span class="${priceClass}">${priceText}</span>
                        </div>
                        <h5 class="card-title">${course.courseName}</h5>
                        <p class="card-text text-muted">${course.description || 'Описание курса отсутствует'}</p>
                    </div>
                    <div class="card-footer bg-transparent">
                        <a href="/courses/${course.id}" class="btn btn-sm btn-primary">Подробнее</a>
                        <small class="text-muted float-end">
                            <i class="far fa-calendar-alt me-1"></i>
                            ${new Date(course.createdAt).toLocaleDateString()}
                        </small>
                    </div>
                </div>
            </div>
        `;
    });

    container.innerHTML = html;
}

// Настройка пагинации
function setupPagination(courses) {
    const totalPages = Math.ceil(courses.length / coursesPerPage);
    const pagination = document.getElementById('pagination');

    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }

    let html = '';

    // Кнопка "Назад"
    html += `
        <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" aria-label="Previous" onclick="changePage(${currentPage - 1})">
                <span aria-hidden="true">&laquo;</span>
            </a>
        </li>
    `;

    // Страницы
    for (let i = 1; i <= totalPages; i++) {
        html += `
            <li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="#" onclick="changePage(${i})">${i}</a>
            </li>
        `;
    }

    // Кнопка "Вперед"
    html += `
        <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
            <a class="page-link" href="#" aria-label="Next" onclick="changePage(${currentPage + 1})">
                <span aria-hidden="true">&raquo;</span>
            </a>
        </li>
    `;

    pagination.innerHTML = html;
}

// Смена страницы
function changePage(page) {
    currentPage = page;
    loadCourses();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', initCoursesPage);