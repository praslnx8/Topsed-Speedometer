package com.bike.race.components.service

import android.os.*
import com.bike.race.components.service.MessengerProtocol.Command
import com.bike.race.uiModels.DashboardData

/**
 * Messenger to communicate to the Service that is bound to the Activity
 */
class ActivityMessenger(
    onStatusUpdated: (dashboardData: DashboardData) -> Unit,
    onRaceFinished: (raceId: Long) -> Unit
) {

    /**
     * Messenger to send message to service
     */
    private var sendingMessenger: Messenger? = null

    /**
     * Messenger to receive reply from service
     */
    private val receivingMessenger = Messenger(
        ReplyHandler(
            onStatusUpdated,
            onRaceFinished
        )
    )

    fun onConnect(messenger: Messenger) {
        this.sendingMessenger = messenger
    }

    fun onDisconnect() {
        this.sendingMessenger = null
    }

    /**
     * Send message to service to start the drive
     */
    fun startDrive() {
        sendMessage(MessengerProtocol.COMMAND_START)
    }

    /**
     * Send message to service to pause the drive
     */
    fun pauseDrive() {
        sendMessage(MessengerProtocol.COMMAND_PAUSE)
    }

    /**
     * Send message to Service to end the drive
     */
    fun stopDrive() {
        sendMessage(MessengerProtocol.COMMAND_STOP)
    }

    /**
     * Handshake for activity to get status when the activity is recreated.
     */
    fun handShake() {
        sendMessage(MessengerProtocol.COMMAND_HANDSHAKE)
    }

    /**
     * sharingKey is optional and should be only sent for syncPomodoro.
     */
    private fun sendMessage(@Command command: Int) {
        val msg = Message.obtain()

        val bundle = Bundle()
        bundle.putInt(MessengerProtocol.COMMAND_TYPE_KEY, command)

        msg.data = bundle
        msg.replyTo = receivingMessenger

        try {
            sendingMessenger?.send(msg)
        } catch (ignore: RemoteException) {
            //Ignore
        }
    }

    internal class ReplyHandler(
        private val onStatusUpdated: (dashboardData: DashboardData) -> Unit,
        private val onRaceFinished: (raceId: Long) -> Unit
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.data?.getInt(MessengerProtocol.REPLY_KEY)) {
                /**
                 * REPLY_DASHBOARD -> Send the status back to activity to show data in speedometer dashboard
                 */
                MessengerProtocol.REPLY_DASHBOARD -> {
                    val dashboardData =
                        msg.data?.getParcelable<DashboardData>(MessengerProtocol.DASHBOARD_DATA_KEY)
                            ?: return
                    onStatusUpdated.invoke(dashboardData)
                }
                /**
                 * REPLY_RACE_FINISH -> Inform Activity that the drive is ended and ask activity to show the detail page.
                 */
                MessengerProtocol.REPLY_RACE_FINISH -> {
                    val raceId = msg.data?.getLong(MessengerProtocol.RACE_ID_KEY)
                    raceId?.let {
                        onRaceFinished(it)
                    }
                }
            }
        }
    }
}