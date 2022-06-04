package com.bike.race.components.service

import androidx.annotation.IntDef

object MessengerProtocol {

    const val DASHBOARD_DATA_KEY = "DASHBOARD_DATA_KEY" //Key to send the dashboard data(Drive status Data) to dashboard page.
    const val RACE_ID_KEY = "RACE_ID_KEY" //Key to send the Drive Id after the Drive is ended.
    const val COMMAND_TYPE_KEY = "COMMAND_TYPE_KEY"
    const val REPLY_KEY = "REPLY_KEY"

    const val COMMAND_HANDSHAKE = 0 //Command code  from Activity to get the current status in case if it is recreated.
    const val COMMAND_START = 3 //Command Code for Service to start the drive.
    const val COMMAND_PAUSE = 4 //Command Code for Service to pause the drive.
    const val COMMAND_STOP = 5 // Command Code for Service to end the drive.

    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
    @IntDef(COMMAND_HANDSHAKE, COMMAND_START, COMMAND_PAUSE, COMMAND_STOP)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Command


    const val REPLY_DASHBOARD = 1 //Reply code for Activity to show the latest status
    const val REPLY_RACE_FINISH = 2 //Reply code for Activity to show drive ended and go to detail page

    @IntDef(REPLY_DASHBOARD, REPLY_RACE_FINISH)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Reply

}