package io.chengine.session

import java.util.concurrent.TimeUnit

interface SessionCacheManager {

    fun create(expirationTime: Int, timeDurationUnit: TimeUnit)

}