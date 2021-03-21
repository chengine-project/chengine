package io.chengine.common

// Boolean extensions

infix fun <T> Boolean.then(action: () -> T): T? {
    return if (this)
        action.invoke()
    else null
}

infix fun <T> T?.`else`(action: () -> T): T {
    return this ?: action.invoke()
}

fun CharSequence?.isNonNullButBlankOrEmpty(): Boolean {
    return this != null && this.isBlank()
}

inline fun <T> T?.isNull(block: () -> Unit) {
    if (this == null)
        block.invoke()
}

inline fun CharSequence?.isNullOrEmpty(block: () -> Unit) {
    if (this == null || this.isBlank())
        block.invoke()
}

fun <T> T?.isNull(): Boolean = this == null