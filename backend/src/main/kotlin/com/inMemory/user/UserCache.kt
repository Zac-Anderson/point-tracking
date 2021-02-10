package com.inMemory.user

import org.springframework.stereotype.Component

@Component
class UserCache {
    private val cache = HashMap<String, InMemoryUser>()

    val size: Int
        get() = cache.size

    operator fun set(key: String, value: InMemoryUser) {
        this.cache[key] = value
    }

    operator fun get(key: String) = this.cache[key]

    fun remove(key: String) = this.cache.remove(key)

    fun clear() = this.cache.clear()
}