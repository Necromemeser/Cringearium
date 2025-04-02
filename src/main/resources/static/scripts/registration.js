document.getElementById('registrationForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    // Сброс ошибок
    resetErrors();

    // Получение значений полей
    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    // Валидация
    let isValid = true;

    if (username.length < 3) {
        document.getElementById('usernameError').textContent = 'Имя пользователя должно содержать минимум 3 символа';
        isValid = false;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        document.getElementById('emailError').textContent = 'Введите корректный email';
        isValid = false;
    }

    if (password.length < 6) {
        document.getElementById('passwordError').textContent = 'Пароль должен содержать минимум 6 символов';
        isValid = false;
    }

    if (password !== confirmPassword) {
        document.getElementById('confirmPasswordError').textContent = 'Пароли не совпадают';
        isValid = false;
    }

    if (!isValid) return;

    // Подготовка данных для отправки
    const userData = {
        username: username,
        email: email,
        password_hash: password,
        matchingPassword: confirmPassword
    };

    console.log(JSON.stringify(userData));

    try {
        const response = await fetch('/api/user/registration', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Ошибка регистрации');
        }

        // Успешная регистрация
        document.getElementById('successAlert').classList.remove('d-none');
        document.getElementById('registrationForm').classList.add('d-none');

        // Перенаправление на страницу входа через 3 секунды
        setTimeout(() => {
            window.location.href = '/login';
        }, 3000);

    } catch (error) {
        document.getElementById('errorMessage').textContent = error.message;
        document.getElementById('errorAlert').classList.remove('d-none');
    }
});

function resetErrors() {
    document.getElementById('usernameError').textContent = '';
    document.getElementById('emailError').textContent = '';
    document.getElementById('passwordError').textContent = '';
    document.getElementById('confirmPasswordError').textContent = '';
    document.getElementById('errorAlert').classList.add('d-none');
}