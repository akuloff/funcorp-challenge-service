# FunCorp challenge service

### RND project for challenge 2020 java/kotlin REST service [challenge](https://funcodechallenge.com/task)

для сервиса необходимо задать переменные окружения:


REDIS_HOST  (хост Redis, по умолчанию localhost)
REDIS_PORT  (порт Redis, по умолчанию )

SERVER_PORT (порт для REST сервисов)
 
CREATE_CRAWLERS=true (флаг инициализации сборщиков media контента, если выключен, то сервис будет работать только как REST)

CRAWLERS_CONFIG_FILE=crawlers.json (путь к файлу конфига сборщиков)

SEDA_QUEUE_SIZE=10000 (размер очереди для загрузчика контента) 
SEDA.CONCURRENT.CONSUMERS=1 (кол-во параллельных обработчиков сборщиков контента из очереди, по умолчанию 1)

LOCAL_STORAGE_PATH  (путь к локальному хранилищу контента - поскольку сейчас с качестве хранилища используется файлы)


Пример файла конфигурации сборщиков находится в корне проекта (файл crawlers.json),
основные важные параметры:

source-id - уникальное название среди всех сборщиков
type - поддерживается пока только "giphy"
interval - интервал в миллисекундах между запусками API
query - строка поиска
deep-scan (true/false) - последовательное постраничное сканирование (если отклюбчено - то будет всегда запрашиваться первая страница)

### механизм добавления нового обработчика:

Добавляется новый маршрут обработки camel (camel-route) типа direct с кодом perform-request-**TYPE**
где type  - тип сборщика из конфига 
на выходе маршрута должен быть в теле список объектов ExtendedFeedRecord, которые должны отдаваться в поток _send-records-list-to-storage_



 
 
