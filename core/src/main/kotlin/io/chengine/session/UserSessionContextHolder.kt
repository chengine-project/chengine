package io.chengine.session

open class UserSessionContextHolder {
    public companion object {

        private val session = ThreadLocal<Session>()

        fun currentSession(): Session? = session.get()

        fun setSession(session: Session?) {
            this.session.set(session)
        }
    }
}