package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        return User.makeUser(fullName, email = email, password = password)
            .also { user ->
                if (map.containsKey(user.login)) throw IllegalArgumentException("A user with this email already exists")
                else map[user.login] = user
                //println(map)
            }
    }

    /*
    Регистрация пользователя через номер телефона
    Необходимо реализовать метод объекта (object UserHolder) для регистрации пользователя через телефон
    Реализуй метод registerUserByPhone(fullName: String, rawPhone: String) возвращающий объект User
    (объект User должен содержать поле accessCode с 6 значным значением состоящим из случайных строчных
    и прописных букв латинского алфавита и цифр от 0 до 9), если пользователь с таким же телефоном
    уже есть в системе необходимо бросить ошибку IllegalArgumentException("A user with this phone already exists")
    валидным является любой номер телефона содержащий первым символом + и 11 цифр и не содержащий буквы,
    иначе необходимо бросить исключение
    IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
     */
    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ): User {
        //
        val phone: String = rawPhone?.replace("[^+\\d]".toRegex(), "")
        val regex = "\\+\\d{11}$".toRegex()

        if (!regex.matches(input = phone))
            throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
        else if (map.containsKey(phone)) throw IllegalArgumentException("A user with this phone already exists")
        else return User.makeUser(fullName, phone = phone)
            .also { user -> map[user.login] = user }
    }


    /*
Авторизация пользователя
Необходимо реализовать метод объекта (object UserHolder) для авторизации по логину и паролю

Реализуй метод loginUser(login: String, password: String) : String возвращающий поле userInfo пользователя
с соответствующим логином и паролем (логин для пользователя phone или email,
пароль соответственно accessCode или password указанный при регистрации методом registerUser)
или возвращающий null если пользователь с указанным логином и паролем не найден (или неверный пароль)
 */
    fun loginUser(login: String, password: String): String? {
        println("loginUser password = $password")
        println("map[login.trim()]? ${map[login.trim()]} ")
        val phone: String = login?.replace("[^+\\d]".toRegex(), "")
        val regex = "\\+\\d{11}$".toRegex()
        //check if it correct phone?
        if (regex.matches(input = phone)) {
            return map[phone]?.run {
                println("loginUser password = $password")
                if (this.accessCode == password.trim()) {
                    println("loginUser() phone ${this.userInfo}")
                    this.userInfo
                } else null
            }
        } else { //if login == email
            return map[login.trim()]?.run {
                println("loginUser email+ password = ${this.userInfo}")
                if (checkPassword(password)) {
                    println("loginUser() check password ${this.userInfo}")
                    this.userInfo
                } else null
            }
        }
    }

    /*
    Запрос кода авторизации
    Необходимо реализовать метод объекта (object UserHolder) для запроса нового кода авторизации пользователя по номеру телефона

    Реализуй метод requestAccessCode(login: String) : Unit, после выполнения данного метода у пользователя
    с соответствующим логином должен быть сгенерирован новый код авторизации и помещен в свойство accessCode,
    соответственно должен измениться и хеш пароля пользователя (вызов метода loginUser должен отрабатывать корректно)
     */
    fun requestAccessCode(login: String): Unit {
        val phone: String = login?.replace("[^+\\d]".toRegex(), "")
        val regex = "\\+\\d{11}$".toRegex()
        //check if it correct phone?
        if (regex.matches(input = phone)) {
            map[phone]?.newAccessCode()
        }
    }


/*
*Импорт из csv
Необходимо реализовать метод объекта (object UserHolder) для импорта пользователей из списка строк
+3
Реализуй метод importUsers(list: List): List, в качестве аргумента принимает список строк
где разделителем полей является ";" данные перечислены в следующем порядке -
Полное имя пользователя; email; соль:хеш пароля; телефон
(Пример: " John Doe ;JohnDoe@unknow.com;[B@7591083d:c6adb4becdc64e92857e1e2a0fd6af84;;")
метод должен вернуть коллекцию список User (Пример возвращаемого userInfo:
firstName: John
lastName: Doe
login: johndoe@unknow.com
fullName: John Doe
initials: J D
email: JohnDoe@unknow.com
phone: null
meta: {src=csv}
), при этом meta должно содержать "src" : "csv", если сзначение в csv строке пустое
то соответствующее свойство в объекте User должно быть null, обратите внимание что salt и hash пароля
в csv разделены ":" , после импорта пользователей вызов метода loginUser должен отрабатывать корректно
(достаточно по логину паролю)
 */

    fun importUsers(list: List<String>): List<User> {

        var listOfUsers: List<User> = listOf()

        for (csvString in list) {
            var fields: List<String> =
                " John Doe ;JohnDoe@unknow.com;[B@7591083d:c6adb4becdc64e92857e1e2a0fd6af84;;"
                    .split(";")
            val fullName: String = fields[0]
            val email: String? = fields[1]
            val (salt, hash) = fields[2].split(":")
            val phone: String? = fields[3]
            val user: User = User.makeUser(
                fullName,
                email,
                password = null,
                phone = phone,
                hash = hash,
                salt = salt
            )
            listOfUsers = listOfUsers.plus(user)
            map[user.login] = user
            println("in for listOfUsers.size ${listOfUsers.size}")
            println("user ${user.userInfo}")
        }


        println("listOfUsers.size ${listOfUsers.size}")
        return listOfUsers

    }



    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

}