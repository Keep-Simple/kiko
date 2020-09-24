package com.kiko.services

class NotificationService {
    fun notifyTenantOfReservation(timeCell: Int, tenantId: Int, flatId: Int) =
        println("[Notification] timeCell: $timeCell in flat: $flatId reserved")

    fun notifyNewTenantOfViewStatus(timeCell: Int, tenantId: Int?, flatId: Int, approved: Boolean) =
        println("[Notification] reservation for tenant: $tenantId in flat: $flatId with status: $approved")

    fun notifyTenantOfCancellation(timeCell: Int, flatId: Int, tenantId: Int?) =
        println("[Notification] reservation cancelled for flat: $flatId with timeCell: $timeCell")
}
