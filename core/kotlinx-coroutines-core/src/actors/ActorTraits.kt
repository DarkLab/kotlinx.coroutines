package kotlinx.coroutines.experimental.actors

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.*
import kotlin.coroutines.experimental.*

/**
 * Actor traits, common for [Actor] and [MonoActor].
 * Actor is a high-level abstraction for [channel][ReceiveChannel] and coroutine, which
 * sequentially processes messages from this channel, having the semantics of
 * the Actor Model: <a href="http://en.wikipedia.org/wiki/Actor_model">http://en.wikipedia.org/wiki/Actor_model</a>
 *
 * Every actor has a [Job] associated with it, so when actor finishes its execution, all started children and launched tasks are cancelled.
 *
 * Any actor has well-defined lifecycle:
 * -- Not started. Note that by default actors are started [lazily][CoroutineStart.LAZY]
 * -- Active. Actor is running and processes incoming messages
 * -- Closing. Actor's channel is closed for new messages, but actor still processes all pending messages,
 *    then cancels its [Job] and invokes [onClose]. Can be triggered by [close] call
 * -- Killed. Actor's channel is closed for new messages, pending messages are not processed and
 *            hang in the channel, [onClose] is invoked. Can be triggered by [kill] call
 *
 * Note:
 * [ActorTraits] doesn't have any variations of `send` method, because different implementations
 * have different ways to expose mailbox to provide static typing.
 */
abstract class ActorTraits {

    /**
     * Job identifying current actor and available from its [coroutineContext]
     *
     * Lifecycle:
     * If job is cancelled, actor is effectively killed
     * If actor is closed, job is cancelled as soon as all messages are processed
     * If actor is killed, job is cancelled immediately
     */
    public abstract val job: Job

    /**
     * Closed the actor and its channel.
     * Before closing, the actor processes all pending messages and
     * then cancels its job (and all its children) and calls [onClose] when job and its children are cancelled
     */
    public abstract fun close()

    /**
     * Kill the actor and its channel without letting the actor to process pending messages.
     * This is the last-ditch way to stop the actor which shouldn't be used normally.
     * It's guaranteed that [onClose] will be called.
     */
    public abstract fun kill()

    /**
     *
     */
    protected open suspend fun onStart() {}

    /**
     * Handler which is invoked when actor is closed or killed.
     * It's guaranteed that on the moment of invocation no more messages will be processed by the actor.
     * This handler is invoked even if actor wasn't started
     */
    protected open fun onClose() {}

    /**
     * Waits until the actor is closed or cancelled
     */
    public suspend fun join(): Unit = job.join()
}
