package ru.skillbranch.kotlinexample

import org.junit.After
import org.junit.Assert
import org.junit.Test
import ru.skillbranch.kotlinexample.extensions.dropLastUntil

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    /**
    Добавьте метод в UserHolder для очистки значений UserHolder после выполнения каждого теста,
    это необходимо чтобы тесты можно было запускать одновременно

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder(){
    map.clear()
    }
     */
    @After
    fun after() {
        UserHolder.clearHolder()
    }

    @Test
    fun register_user_success() {
        val holder = UserHolder
        val user = holder.registerUser("John Doe", "John_Doe@unknown.com", "testPass")
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: john_doe@unknown.com
            fullName: John Doe
            initials: J D
            email: John_Doe@unknown.com
            phone: null
            meta: {auth=password}
        """.trimIndent()

        Assert.assertEquals(expectedInfo, user.userInfo)
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_fail_blank() {
        val holder = UserHolder
        holder.registerUser("", "John_Doe@unknown.com", "testPass")
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_fail_illegal_name() {
        val holder = UserHolder
        holder.registerUser("John Jr Doe", "John_Doe@unknown.com", "testPass")
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_fail_illegal_exist() {
        val holder = UserHolder
        holder.registerUser("John Doe", "John_Doe@unknown.com", "testPass")
        holder.registerUser("John Doe", "John_Doe@unknown.com", "testPass")
    }

    @Test
    fun register_user_by_phone_success() {
        val holder = UserHolder
        val user = holder.registerUserByPhone("John Doe", "+7 (917) 971 11-11")
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: +79179711111
            fullName: John Doe
            initials: J D
            email: null
            phone: +79179711111
            meta: {auth=sms}
        """.trimIndent()

        Assert.assertEquals(expectedInfo, user.userInfo)
        Assert.assertNotNull(user.accessCode)
        Assert.assertEquals(6, user.accessCode?.length)
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_by_phone_fail_blank() {
        val holder = UserHolder
        holder.registerUserByPhone("", "+7 (917) 971 11-11")
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_by_phone_fail_illegal_name() {
        val holder = UserHolder
        holder.registerUserByPhone("John Doe", "+7 (XXX) XX XX-XX")
    }

    @Test(expected = IllegalArgumentException::class)
    fun register_user_failby_phone_illegal_exist() {
        val holder = UserHolder
        holder.registerUserByPhone("John Doe", "+7 (917) 971-11-11")
        holder.registerUserByPhone("John Doe", "+7 (917) 971-11-11")
    }

    @Test
    fun login_user_success() {
        val holder = UserHolder
        holder.registerUser("John Doe", "John_Doe@unknown.com", "testPass")
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: john_doe@unknown.com
            fullName: John Doe
            initials: J D
            email: John_Doe@unknown.com
            phone: null
            meta: {auth=password}
        """.trimIndent()

        val successResult = holder.loginUser("john_doe@unknown.com", "testPass")

        Assert.assertEquals(expectedInfo, successResult)
    }

    @Test
    fun login_user_by_phone_success() {
        val holder = UserHolder
        val user = holder.registerUserByPhone("John Doe", "+7 (917) 971-11-11")
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: +79179711111
            fullName: John Doe
            initials: J D
            email: null
            phone: +79179711111
            meta: {auth=sms}
        """.trimIndent()

        val successResult = holder.loginUser("+7 (917) 971-11-11", user.accessCode!!)

        Assert.assertEquals(expectedInfo, successResult)
    }

    @Test
    fun login_user_fail() {
        val holder = UserHolder
        holder.registerUser("John Doe", "John_Doe@unknown.com", "testPass")

        val failResult = holder.loginUser("john_doe@unknown.com", "test")

        Assert.assertNull(failResult)
    }

    @Test
    fun login_user_not_found() {
        val holder = UserHolder
        holder.registerUser("John Doe", "John_Doe@unknown.com", "testPass")

        val failResult = holder.loginUser("john_cena@unknown.com", "test")

        Assert.assertNull(failResult)
    }

    @Test
    fun request_access_code() {
        val holder = UserHolder
        val user = holder.registerUserByPhone("John Doe", "+7 (917) 971-11-11")
        val oldAccess = user.accessCode
        holder.requestAccessCode("+7 (917) 971-11-11")

        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: +79179711111
            fullName: John Doe
            initials: J D
            email: null
            phone: +79179711111
            meta: {auth=sms}
        """.trimIndent()

        val successResult = holder.loginUser("+7 (917) 971-11-11", user.accessCode!!)

        Assert.assertNotEquals(oldAccess, user.accessCode!!)
        Assert.assertEquals(expectedInfo, successResult)
    }

    /*
    listOf(1, 2, 3).dropLastUntil{ it==2 } // [1]
    "House Nymeros Martell of Sunspear".split(" ")
    .dropLastUntil{ it == "of" } // [House, Nymeros, Martell])
     */
    @Test
    fun test_dropLastUntil() {
        val sRes = listOf(1, 2, 3).dropLastUntil { it == 2 }
        val successResult = "House Nymeros Martell of Sunspear".split(" ")
            .dropLastUntil { it == "of" }
        Assert.assertEquals(listOf(1), sRes)
        Assert.assertEquals(listOf("House", "Nymeros", "Martell"), successResult)
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
    @Test
    fun test_importUsers() {
        val holder = UserHolder
        val expectedUsers = holder.importUsers(
            listOf(
                " John    Doe ;JohnDoe@list.ru;[B@7591083d:c6adb4becdc64e92857e1e2a0fd6af84;;",
                " John;;[B@77a567e1:a07e337973f9ab704118c73ff827a695;+7 (900) 971-11-11;"
            )
        )
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: johndoe@list.ru
            fullName: John Doe
            initials: J D
            email: JohnDoe@list.ru
            phone: null
            meta: {src=csv}
        """.trimIndent()

        val expectedInfo2 = """
            firstName: John
            lastName: null
            login: +79009711111
            fullName: John
            initials: J
            email: null
            phone: +79009711111
            meta: {src=csv}
        """.trimIndent()

        val successResult = holder.loginUser("johndoe@list.ru", "testPass")
        val failResult = holder.loginUser("JohnDoe@gmail.ru", "invalidPass")

        Assert.assertEquals(null, failResult)
        Assert.assertEquals(expectedInfo, successResult)
        Assert.assertEquals(expectedInfo, expectedUsers.first().userInfo)
        Assert.assertEquals(expectedInfo2, expectedUsers.last().userInfo)
/*
        val holder = UserHolder
        val userList: List<String> = listOf(
            " Test test ;test@test.com;[B@7591083d:c6adb4becdc64e92857e1e2a0fd6af84;;",
            " John Doe ;JohnDoe@unknow.com;[B@7591083d:c6adb4becdc64e92857e1e2a0fd6af84;;"
        )
        val successResult: List<User> = holder.importUsers(userList)
        val expectedInfo = """
    firstName: John
    lastName: Doe
    login: johndoe@unknow.com
    fullName: John Doe
    initials: J D
    email: JohnDoe@unknow.com
    phone: null
    meta: {src=csv}
        """.trimIndent()
        val loginResult = holder.loginUser("johndoe@unknow.com", "testPass")

        //    .dropLastUntil { it == "of" }
        Assert.assertEquals(2, successResult.size)
        Assert.assertEquals(expectedInfo, successResult[1].userInfo)
        Assert.assertNotEquals(expectedInfo, successResult[0].userInfo)

        Assert.assertEquals(expectedInfo, loginResult)
*/

    }
}