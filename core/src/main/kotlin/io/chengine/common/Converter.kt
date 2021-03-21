package io.chengine.common

interface Converter<T, G> {

    fun convert(t: T): G

}