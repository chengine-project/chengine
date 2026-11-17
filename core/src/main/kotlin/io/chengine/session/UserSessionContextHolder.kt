package io.chengine.session

open class UserSessionContextHolder {
    companion object {
        private val session = ThreadLocal<Session>()
        private val manager = ThreadLocal<SessionManager>()

        fun currentSession(): Session? = session.get()
        fun setSession(session: Session?) { if (session == null) this.session.remove() else this.session.set(session) }
        internal fun bind(sessionManager: SessionManager) { manager.set(sessionManager) }
        internal fun start(value: Session) {
            checkNotNull(manager.get()) { "No active request session context" }.putSessionBy(value.sessionKey(), value)
            setSession(value)
        }
        internal fun end() {
            session.get()?.let { checkNotNull(manager.get()) { "No active request session context" }.invalidateSessionBy(it.sessionKey()) }
            session.remove()
        }
        internal fun flush() {
            session.get()?.let { checkNotNull(manager.get()) { "No active request session context" }.putSessionBy(it.sessionKey(), it) }
        }
        internal fun clear() { session.remove(); manager.remove() }
    }
}
