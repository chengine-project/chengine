package io.chengine.session

interface SessionManager {

    fun getSessionBy(sessionKey: SessionKey): Session?

    fun putSessionBy(sessionKey: SessionKey, session: Session): Session

    fun invalidateSessionBy(sessionKey: SessionKey)

}