#!/bin/sh

# Запускаем Ollama в фоновом режиме
ollama serve &

# Ждём запуска сервера (альтернатива без curl)
sleep 5  # Даём время на запуск сервера

# Проверяем наличие модели и загружаем при необходимости
if ! ollama list | grep -q "deepseek-r1:7b"; then
  echo "Загружаем модель deepseek-r1:7b..."
  ollama run deepseek-r1:7b
fi

# Оставляем процесс ollama serve работать
wait