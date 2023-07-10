## Пример работы с JPA (Hibernate)

Имеются 3 таблицы, содержащие список пользователей, ролей и их связь. По условию задания, отношение между пользователем и ролью задано как многие-ко-многим.

```
id|user_name          |roles_list                      
--+-------------------+--------------------------------
 1|Lionel C. Mathena  |READ, WRITE, UPLOAD, INFO, STATS
 2|Pierre J. Gouge    |READ, WRITE, UPLOAD             
 3|Rose D. Kent       |READ, WRITE, UPLOAD             
 4|Beverly S. Bingaman|READ, WRITE, UPLOAD             
 5|Daniel V. Morones  |INFO, STATS                     
 6|Thomas E. Bynum    |                                
```

Задачи:

- Создавать / изменять / удалять роль.
- Создавать / изменять / удалять пользователя.
- Назначать роли пользователю.
- Получать список пользователей обладающих ролью.
- Получать список ролей отдельного пользователя.

Для демонстрации, проблема N+1 запроса решена 3-мя способами:

- join fetch запрос в UserService.
- NamedEntityGraph задан в классе User.
- EntityGraph создается в момент вызова в UserService.

Настройки файла persistence.xml:

- Задано пространство имен для версии JPA 3.
- Задан многострочный загрузчик SQL скриптов.
- При создании базы используется файл `META-INF/db_init.sql`
- Для отладки можно раскомментировать строки в секции Debug


### Запуск
```shell
mvn clean compile exec:java
```


### Ожидаемый вывод
```
Создан пользователь: User{id=50, name='James M. Saunders'}, [Role{READ}, Role{WRITE}]
Пользователи с ролью 'READ': 
User{id=1, name='Lionel C. Mathena'}
User{id=2, name='Pierre J. Gouge'}
User{id=3, name='Rose D. Kent'}
User{id=4, name='Beverly S. Bingaman'}
User{id=50, name='James M. Saunders'}
Создана новая роль: Role{WEB}
Роли пользователя User{id=50, name='James M. Saunders'} были изменены.
Текущие роли пользователя User{id=50, name='James M. Saunders'}: [Role{WEB}, Role{STATS}, Role{INFO}, Role{WRITE}]
Роли пользователя с именем 'Pierre J. Gouge': [Role{READ}, Role{UPLOAD}, Role{WRITE}]
Пользователь удален: User{id=50, name='James M. Saunders'}
Неиспользуемая роль WEB была успешно удалена
```


### Тестирование

Для запуска интеграционных тестов с использованием TestContainers:
```
mvn failsafe:integration-test
```