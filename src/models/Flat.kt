package com.kiko.models

/*
 * viewSchedule represents the time viewing slots
 * & contains Reservation objects
 */
class Flat(val currentTenantId: Int?) {
    val viewSchedule = Array(11 * 3 * 7) { Reservation() }

    fun reserveView(timeCell: Int, newTenantId: Int): Boolean {
        val (tenantId, _, banned) = viewSchedule[timeCell]

        if (tenantId != null || banned) return false

        synchronized(this) {
            viewSchedule[timeCell].tenantId = newTenantId
        }

        return true
    }

    fun cancelView(timeCell: Int) = synchronized(this) {
        viewSchedule[timeCell].run {
            tenantId = null
            approved = false
        }
    }

    fun rejectView(timeCell: Int): Int? = synchronized(this) {
        viewSchedule[timeCell]
            .takeIf { it.tenantId != null }
            ?.apply {
                approved = false
                banned = true
                tenantId = null
            }
            ?.tenantId
    }

    fun approveView(timeCell: Int): Int? = synchronized(this) {
        viewSchedule[timeCell]
            .takeIf { it.tenantId != null }
            ?.apply {
                approved = true
                banned = false
            }
            ?.tenantId
    }
}

