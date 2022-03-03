package ru.skillbranch.kotlinexample.extensions

fun List<String>.dropLastUntil(predicate: (String) -> Boolean): List<String> =
    take(indexOf(find { predicate(it) }))
