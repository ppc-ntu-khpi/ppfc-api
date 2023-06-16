package org.ppfc.api.common

object StringResource {
    const val welcomeToPpfcServer = "Ласкаво просимо на сервер PPFC!"
    const val notAuthorized = "Не авторизовано"
    const val internalError = "Внутрішня помилка"
    const val resourceNotFound = "Ресурс не знайдено."
    const val tokenIsNotValidOrExpired = "Токен недійсний або термін його дії минув."
    const val idPathParameterNotFound = "Параметр шляху ідентифікатора 'id' відсутній."
    const val dateParameterNotFound = "Параметр 'date' відсутній."
    const val idsPathParameterNotFound = "Параметр шляху ідентифікатора 'ids' відсутній."
    const val argumentFormatError = "Невірний формат аргумента."
    const val fieldsSubjectIdAndEventNameAreNull =
        "У моделі даних має бути хоча б одне ненульове поле subjectId або eventName."
    const val fieldsGroupIdAndTeacherIdAreNull =
        "У моделі даних має бути хоча б одне ненульове поле groupId або teacherId."
    const val invalidDateFormat = "Формат дати невірний."
    const val error = "Помилка"
}