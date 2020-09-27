package com.kiko.models

import TimeSlot

/*
 * schedule represents the time viewing slots
 * & contains Reservation objects
 */
class Flat(val currentTenantId: Int?) {
    val schedule = ViewSchedule()

    @Synchronized
    fun reserveView(timeSlot: TimeSlot, newTenantId: Int): Boolean {
        schedule.get(timeSlot)?.run {
            if (tenantId == null && !banned) {
                this.tenantId = newTenantId
                return true
            }
        }
        return false
    }

    @Synchronized
    fun rejectView(timeSlot: TimeSlot) = schedule.get(timeSlot)
        ?.takeIf { it.tenantId != null }
        ?.apply {
            approved = false
            banned = true
        }?.tenantId

    @Synchronized
    fun approveView(timeSlot: TimeSlot) = schedule.get(timeSlot)
        ?.takeIf { it.tenantId != null }
        ?.apply {
            approved = true
            banned = false
        }?.tenantId

    @Synchronized
    fun cancelView(timeSlot: TimeSlot) = schedule.get(timeSlot)
        ?.apply {
            tenantId = null
            approved = false
        }
}

