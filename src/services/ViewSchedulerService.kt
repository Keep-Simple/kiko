package com.kiko.services

import ReserveDto
import ReserveShortDto
import com.kiko.models.Flat

class ViewSchedulerService(
    private val flats: MutableMap<Int, Flat>,
    private val notificationService: NotificationService
) {

    fun reserve(dto: ReserveDto): Boolean {
        val (tenantId, flatId, timeCell) = dto
        val flat = flats[flatId]
        if (flat != null && tenantId != flat.currentTenantId && flat.reserveView(timeCell, tenantId)) {
            notificationService.notifyTenantOfReservation(timeCell, tenantId, flatId)
            return true;
        }
        return false;
    }

    fun cancelReservation(dto: ReserveShortDto) {
        val (flatId, timeCell) = dto
        flats[flatId]?.run {
            cancelView(timeCell)
            notificationService.notifyTenantOfCancellation(timeCell, flatId, currentTenantId)
        }
    }

    fun rejectReservation(dto: ReserveShortDto) {
        val (flatId, timeCell) = dto
        flats[flatId]?.run {
            val tenantId = rejectView(timeCell)
            notificationService.notifyNewTenantOfViewStatus(timeCell, tenantId, flatId, false)
        }
    }

    fun approveReservation(dto: ReserveShortDto) {
        val (flatId, timeCell) = dto
        flats[flatId]?.run {
            val tenantId = approveView(timeCell)
            notificationService.notifyNewTenantOfViewStatus(timeCell, tenantId, flatId, true)
        }
    }
}
