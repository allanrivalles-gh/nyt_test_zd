package com.theathletic.extension

fun <T, R : Comparable<R>> MutableList<T>.uniqueBy(selector: (T) -> R?) {
    val oldData: MutableList<T> = ArrayList()
    oldData.addAll(this)
    this.clear()
    this.addAll(oldData.distinctBy(selector))
}

@PublishedApi
internal fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int = if (this is Collection<*>) this.size else default

infix fun <T, R> Iterable<T>.merge(other: Iterable<R>): List<Pair<T?, R?>> {
    return merge(other) { t1, t2 -> t1 to t2 }
}

inline fun <T, R, V> Iterable<T>.merge(other: Iterable<R>, transform: (a: T?, b: R?) -> V): List<V> {
    val first = iterator()
    val second = other.iterator()
    val list = ArrayList<V>(maxOf(collectionSizeOrDefault(10), other.collectionSizeOrDefault(10)))
    while (first.hasNext() || second.hasNext()) {
        list.add(transform(if (first.hasNext()) first.next() else null, if (second.hasNext()) second.next() else null))
    }
    return list
}

fun <T> MutableCollection<T>.addIf(item: T, condition: (T) -> Boolean) {
    if (condition.invoke(item)) {
        add(item)
    }
}

fun <T> MutableCollection<T>.addAll(vararg items: T) {
    addAll(items)
}

fun <T> MutableCollection<T>.replaceAllWith(iterable: Iterable<T>) {
    clear()
    addAll(iterable)
}

inline fun <T, R> Iterable<T>.flatMapIndexed(transform: (Int, T) -> Iterable<R>): List<R> {
    val list = mutableListOf<R>()

    for ((index, item) in this.withIndex()) {
        val transformed = transform(index, item)
        list.addAll(transformed)
    }

    return list
}

inline fun <E : Any?, T : Collection<E>> T.ifEmptyDo(block: (T) -> Unit) {
    if (isEmpty()) {
        block(this)
    }
}

fun <T> List<T>.swap(fromIndex: Int, toIndex: Int) = toMutableList().apply {
    if (indices.contains(fromIndex) && indices.contains(toIndex)) {
        val tmp = this[fromIndex]
        this[fromIndex] = this[toIndex]
        this[toIndex] = tmp
    }
}

fun List<String>.filterSearchMatches(searchText: String): Boolean {
    for (text in this) {
        val isNotBlank = text.isNotBlank()
        val containsSearchText = text.contains(searchText, ignoreCase = true)
        val hasInitials = (searchText.length >= 2 && text.splitInitials().contains(searchText, ignoreCase = true))
        if (isNotBlank && containsSearchText || hasInitials) {
            return true
        }
    }
    return false
}