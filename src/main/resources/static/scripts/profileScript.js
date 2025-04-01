// Обновление аватара при выборе файла
document.getElementById('avatarUpload').addEventListener('change', function(e) {
    if (e.target.files && e.target.files[0]) {
        const reader = new FileReader();

        reader.onload = function(event) {
            const profileImage = document.getElementById('profileImage');
            if (profileImage) {
                profileImage.src = event.target.result;
            } else {
                // Если изображения не было (был дефолтный аватар)
                const avatarContainer = e.target.closest('.file-upload');
                const defaultAvatar = avatarContainer.querySelector('.default-avatar');
                if (defaultAvatar) {
                    defaultAvatar.remove();
                    const newImg = document.createElement('img');
                    newImg.src = event.target.result;
                    newImg.className = 'profile-pic rounded-circle';
                    newImg.id = 'profileImage';
                    newImg.alt = 'Аватар пользователя';
                    avatarContainer.insertBefore(newImg, avatarContainer.firstChild);
                }
            }

            // Здесь можно добавить AJAX-запрос для сохранения изображения
        };

        reader.readAsDataURL(e.target.files[0]);
    }
});

// Валидация формы редактирования
const editForm = document.querySelector('#editProfileModal form');
if (editForm) {
    editForm.addEventListener('submit', function(e) {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (password && password !== confirmPassword) {
            e.preventDefault();
            alert('Пароли не совпадают!');
        }
    });
}