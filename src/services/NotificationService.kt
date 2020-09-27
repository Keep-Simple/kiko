package com.kiko.services

import TimeSlot

class NotificationService {
    fun notifyTenantOfReservation(timeSlot: TimeSlot, tenantId: Int, flatId: Int) =
        println("[Notification] timeSlot: $timeSlot in flat: $flatId reserved")

    fun notifyNewTenantOfViewStatus(timeSlot: TimeSlot, tenantId: Int?, flatId: Int, approved: Boolean) =
        println("[Notification] reservation for tenant: $tenantId in flat: $flatId & timeSlot: $timeSlot with status: $approved")

    fun notifyTenantOfCancellation(timeSlot: TimeSlot, flatId: Int, tenantId: Int?) =
        println("[Notification] reservation cancelled for flat: $flatId with timeSlot: $timeSlot")
}
