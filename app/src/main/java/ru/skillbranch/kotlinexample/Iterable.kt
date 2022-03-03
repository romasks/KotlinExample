package ru.skillbranch.kotlinexample

fun List<String>.dropLastUntil(predicate: (String) -> Boolean): List<String> =
    take(indexOf(find { predicate(it) }))
