package com.kiko.models

import TimeSlot

/*
 * schedule represents the time viewing slots
 * & contains Reservation objects
 */
class Flat(val currentTenantId: Int?) {
    val schedule = ViewSchedule()

    fun reserveView(timeSlot: TimeSlot, newTenantId: Int): Boolean {
        schedule.get(timeSlot)?.run {
            if (tenantId == null && !banned) {
                synchronized(this) {
                    this.tenantId = newTenantId
                }
                return true
            }
        }
        return false
    }

    fun cancelView(timeSlot: TimeSlot) = synchronized(this) {
        schedule.get(timeSlot)?.run {
            tenantId = null
            approved = false
        }
    }

    fun rejectView(timeSlot: TimeSlot): Int? = synchronized(this) {
        schedule.get(timeSlot)
            ?.takeIf { it.tenantId != null }
            ?.apply {
                approved = false
                banned = true
                tenantId = null
            }
            ?.tenantId
    }

    fun approveView(timeSlot: TimeSlot): Int? = synchronized(this) {
        schedule.get(timeSlot)
            ?.takeIf { it.tenantId != null }
            ?.apply {
                approved = true
                banned = false
            }
            ?.tenantId
    }
}

