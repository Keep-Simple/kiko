package com.kiko.services

import com.kiko.models.Flat

class ViewSchedulerService(
    private val flats: MutableMap<Int, Flat>,
    private val notificationService: NotificationService
) {

    fun reserve(flatId: Int, timeCell: Int, tenantId: Int): Boolean {
        val flat = flats[flatId]
        if (flat != null && tenantId != flat.currentTenantId && flat.reserveView(timeCell, tenantId)) {
            notificationService.notifyTenantOfReservation(timeCell, tenantId, flatId)
            return true;
        }
        return false;
    }

    fun cancelReservation(flatId: Int, timeCell: Int) {
        flats[flatId]?.run {
            cancelView(timeCell)
            notificationService.notifyTenantOfCancellation(timeCell, flatId, currentTenantId)
        }
    }

    fun rejectReservation(flatId: Int, timeCell: Int) {
        flats[flatId]?.run {
            val tenantId = rejectView(timeCell)
            notificationService.notifyNewTenantOfViewStatus(timeCell, tenantId, flatId, false)
        }
    }

    fun approveReservation(flatId: Int, timeCell: Int) {
        flats[flatId]?.run {
            val tenantId = approveView(timeCell)
            notificationService.notifyNewTenantOfViewStatus(timeCell, tenantId, flatId, true)
        }
    }
}
