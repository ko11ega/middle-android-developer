package ru.skillbranch.skillarticles.extensions

/*
groupByBounds
Необходимо реализовать extension функцию для группировки результата поиска по интервалам
указанным в коллекции bounds. Количество выходных элементов должно быть рано количеству bounds
+1
Реализуй fun List для группировки результата поиска по интервалам указанным в коллекции bounds.
Количество выходных элементов должно быть рано количеству bounds
Пример: searchResult = [(2,5), (8,20), (22,30), (45,50), (70,100)]
bounds = [(0,10), (10,30), (30,50), (50,60), (60,100)]
result = [[(2, 5), (8, 10)], [(10, 20), (22, 30)], [(45, 50)], [], [(70, 100)]]
 */
// TODO ext/List.kt video:2:07:18
fun List<Pair<Int,Int>>.groupByBounds(bounds: List<Pair<Int,Int>>): MutableList<List<Pair<Int,Int>>>{
    val result : MutableList<List<Pair<Int,Int>>> = mutableListOf()
    // проходим по всем элементам bounds
    // добавляем в список все диапазоны searchResult целиком входящие в текущий bounds
    // из диапазонов searchResult пересекающихся с границами текущей bounds порождаем 2 диапазона
    //  беря границу из текущей bounds

    bounds.forEach{(boundStart, boundEnd) ->
        var boundEl : MutableList<Pair<Int,Int>> = mutableListOf()

        this.forEach {(searchResultStart, searchResultEnd) ->
            // если текущие границы searchResult целиком входят в текущие границы bounds
            if (boundStart<=searchResultStart && boundEnd>=searchResultEnd){
                boundEl.add(Pair(searchResultStart,searchResultEnd))
            } else
            // если текущие границы searchResult целиком за пределами текущих границ bounds
            if (boundStart>searchResultEnd || boundEnd<searchResultStart){
                //boundEl.add(Pair(searchResultStart,searchResultEnd))
            } else
            // если searchResultStart >= boundStart, а searchResultEnd больше boundEnd
            if (searchResultStart>=boundStart && searchResultEnd > boundEnd){
                boundEl.add(Pair(searchResultStart,boundEnd))
            } else
            // если найденное продолжается с начала дипазона bound
            if (searchResultStart < boundStart && searchResultEnd > boundStart){
                boundEl.add(Pair(boundStart,searchResultEnd))
                }
        }
        result.add(boundEl)
    }

    return result
}

