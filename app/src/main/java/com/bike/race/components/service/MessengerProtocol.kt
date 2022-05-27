package com.bike.race.components.service

import androidx.annotation.IntDef

object MessengerProtocol {

    const val DASHBOARD_DATA_KEY = "DASHBOARD_DATA_KEY"
    const val RACE_ID_KEY = "RACE_ID_KEY"
    const val COMMAND_TYPE_KEY = "COMMAND_TYPE_KEY"
    const val REPLY_KEY = "REPLY_KEY"

    const val COMMAND_HANDSHAKE = 0
    const val COMMAND_START = 3
    const val COMMAND_PAUSE = 4
    const val COMMAND_STOP = 5

    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
    @IntDef(COMMAND_HANDSHAKE, COMMAND_START, COMMAND_PAUSE, COMMAND_STOP)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Command


    const val REPLY_DASHBOARD = 1
    const val REPLY_RACE_FINISH = 2

    @IntDef(REPLY_DASHBOARD, REPLY_RACE_FINISH)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Reply

}