package com.bike.race.components.service

import android.os.*
import com.bike.race.components.service.MessengerProtocol.Command
import com.bike.race.uiModels.DashboardData

/**
 * Messenger to communicate to Activity that is bounded to this service.
 */
class ServiceMessenger(
    commandCallback: ((@Command Int) -> Unit)
) {
    private var sendingMessenger: Messenger? = null
    private val receivingMessenger = Messenger(
        ReplyHandler(
            commandCallback
        ) { messenger ->
            sendingMessenger = messenger
        }
    )

    fun getBinder(): IBinder = receivingMessenger.binder

    /**
     * Send current Dashboard data(Drive Data including current position, Top Speed, Distance etc)
     */
    fun sendDashboardData(dashboardData: DashboardData) {
        val bundle = Bundle()
        bundle.putInt(MessengerProtocol.REPLY_KEY, MessengerProtocol.REPLY_DASHBOARD)
        bundle.putParcelable(MessengerProtocol.DASHBOARD_DATA_KEY, dashboardData)
        sendMessage(bundle)
    }

    /**
     * Inform the drive is ended with the drive id that is stored in the DB so that the Activity will show the Detail page.
     */
    fun sendRaceFinished(raceId: Long?) {
        val bundle = Bundle()
        bundle.putInt(MessengerProtocol.REPLY_KEY, MessengerProtocol.REPLY_RACE_FINISH)
        bundle.putLong(MessengerProtocol.RACE_ID_KEY, raceId ?: -1L)
        sendMessage(bundle)
    }

    private fun sendMessage(bundle: Bundle) {
        val msg = Message.obtain()

        msg.data = bundle
        msg.replyTo = receivingMessenger

        try {
            sendingMessenger?.send(msg)
        } catch (ignore: RemoteException) {
            //Ignore
        }
    }

    internal class ReplyHandler(
        private val commandCallback: ((@Command Int) -> Unit),
        private val replyReceiverCallback: ((replyMessenger: Messenger?) -> Unit)
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val command = msg.data?.getInt(MessengerProtocol.COMMAND_TYPE_KEY)
            replyReceiverCallback.invoke(msg.replyTo)

            command?.let {
                commandCallback(command)
            }
        }
    }
}