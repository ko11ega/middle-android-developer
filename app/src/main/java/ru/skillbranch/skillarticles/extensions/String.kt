package ru.skillbranch.skillarticles.extensions

/*
indexesOf
Необходимо реализовать функцию расширения (String.kt) возаращающую список индексов
вхождения подстроки аргумента в исходную строку
+1
Реализуй функцию расширения fun String.indexesOf(substr: String, ignoreCase: Boolean = true): List,
в качестве аргумента принимает подстроку и флаг - учитывать или нет регистр подстроки
при поиске по исходной строке. Возвращает список позиций вхождений подстроки в исходную строку.
Пример: "lorem ipsum sum".indexesOf("sum") // [8, 12]

 */

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    var result : MutableList<Int> = mutableListOf()
    var matchIndex : Int = -1
    if(this == null || substr== "") return result
    while(this.indexOf(substr, matchIndex, ignoreCase)>=0) {
        matchIndex = this.indexOf(substr, matchIndex+1, ignoreCase)
        if (matchIndex==-1) break
        result.add(matchIndex)
    }
    return result
}
