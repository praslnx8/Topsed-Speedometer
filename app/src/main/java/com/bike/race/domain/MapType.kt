package com.bike.race.domain

enum class MapType(val value: Int) {
    NORMAL(0),
    SATELLITE(1);

    override fun toString(): String = value.toString()
}