# Тестовое задание
    Создать сервис, который обращается к сервису курсов валют https://docs.openexchangerates.org,
    и отдает gif в ответ: если курс по отношению к рублю за сегодня стал выше вчерашнего, 
    то отдаем рандомную отсюда https://giphy.com/search/rich, если ниже - отсюда https://giphy.com/search/broke
    
    jar файл:
      $ cd /{yourWorkDir} - перейти в рабочую директорию
      $ mkdir app - создать директорию для приложения
      $ cd app
      $ git clone https://github.com/antleva/test-task-app.git - скопировать проект из репозитория
      $ java -jar testTaskApplication-0.0.1-SNAPSHOT.jar - запуск приложения
      в браузере в поле адреса ввести http://localhost:8080
   
    загрузка образа из DockerHub:
      $ docker push antonlevanov/test-task-app:0.1 - загрузка образа
      $ docker run -d -p 8080:8080 --name test-task-app test-task-app:0.1 - запуск контейнера в фоновом режиме, 
      порт для доступа с контейнеру 8080, 
      имя контейнера test-task-app
      в браузере в поле адреса ввести http://localhost:8080
