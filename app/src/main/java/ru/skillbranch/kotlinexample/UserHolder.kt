package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        if (!map.containsKey(email.lowercase())) {
            val user = User.makeUser(fullName, email = email, password = password)
            map[user.login] = user
            return user
        } else {
            throw IllegalArgumentException("A user with this email already exists")
        }
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        if (map.containsKey(rawPhone.phoneToLogin())) {
            throw IllegalArgumentException("A user with this phone already exists")
        } else {
            val user = User.makeUser(fullName, phone = rawPhone)
            if (user.login.length == 12) {
                map[user.login] = user
                return user
            } else {
                throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
            }
        }
    }

    fun loginUser(login: String, password: String): String? =
        (map[login.trim()] ?: map[login.phoneToLogin()])?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }

    fun requestAccessCode(login: String) {
        val phone = login.phoneToLogin()
        val user = map[phone]!!
        val code = user.generateAccessCode()
        user.changePassword(user.accessCode!!, code)
        map[phone] = user
    }

    fun importUsers(lines: List<String>): List<User?> {
        val users = arrayListOf<User?>()
        lines.forEach { line ->
            if (line.isBlank()) {
                users.add(null)
            } else {
                val (fullName, email, salt_hash, phone) = line.split(";")
                users.add(User.importUser(fullName, email, salt_hash, phone))
            }
        }
        return users
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    private fun String.phoneToLogin() = replace("""[^+\d]""".toRegex(), "")
}