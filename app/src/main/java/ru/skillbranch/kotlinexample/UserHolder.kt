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





    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

}