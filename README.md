
# blockchain

1. Врач заносит информацию по человеку на смарт контракт (анализы, обследования, осмотры,рецепты)
2. В идеале эти данные шифруются врач и пациент добавляются как владельцы политики,после чего пациент может по своему желанию открывать информацию 3-м лицам (Пока что не удалось реализовать)
3. Минздрав сертифицирует аптеки врачей, добавляет лекарства в список запрещенных
4. В аптеке могут запустить проверку рецепта пациента,  учитывая даты начала/конца, квалификацию врача - не реализовано, лицензию врача

Сущности, хранимые в сети имеют следующую структуру:
![alt text](https://github.com/avpodtikhov/blockchain/blob/ba33c2fba030a157747ee2c80adbc5cb955a6717/%D0%A1%D0%BD%D0%B8%D0%BC%D0%BE%D0%BA%20%D1%8D%D0%BA%D1%80%D0%B0%D0%BD%D0%B0%202021-03-31%20%D0%B2%2003.03.34.png)

Текущий функционал смарт-контракта:
1. addDrugs - Добавление аптекой на склад лекарств, не запрещенных минздравом
2. addProtocol - Добавление врачом нового осмотра в историю болезни пациента,
3. addCommonInfo - Добавление "Титульного листа" в медкарту
4. addTest - Добавление врачом результатов анализов
5. addPrescription - Добавление назначения, может только врач с действующей лицензией, необходимо указать срок действия (нельзя добавить задним числом)
6. checkPrescription - Проверка аптекой данных рецептов
7. disableLicense - Функция доступная только суперпользователю, дли изменения состояния лицензий