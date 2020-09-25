package com.kiko.services

import ReserveDto
import ReserveShortDto
import com.kiko.models.Flat

class ViewSchedulerService(
    private val flats: Map<Int, Flat>,
    private val notificationService: NotificationService
) {
    fun reserve(dto: ReserveDto): Boolean {
        val (tenantId, flatId, timeCell) = dto
        flats[flatId]
            ?.takeIf { tenantId != it.currentTenantId }
            ?.run {
                if (reserveView(timeCell, tenantId)) {
                    notificationService.notifyTenantOfReservation(timeCell, tenantId, flatId)
                    return true
                }
            }
        return false
    }

    fun cancelReservation(dto: ReserveShortDto) {
        val (flatId, timeCell) = dto
        flats[flatId]?.run {
            cancelView(timeCell)
            notificationService.notifyTenantOfCancellation(timeCell, flatId, currentTenantId)
        }
    }

    fun rejectReservation(dto: ReserveShortDto): Boolean {
        val (flatId, timeCell) = dto
        flats[flatId]?.run {
            rejectView(timeCell)?.let {
                notificationService.notifyNewTenantOfViewStatus(timeCell, it, flatId, false)
                return true
            }
        }
        return false
    }

    fun approveReservation(dto: ReserveShortDto): Boolean {
        val (flatId, timeCell) = dto
        flats[flatId]?.run {
            approveView(timeCell)?.let {
                notificationService.notifyNewTenantOfViewStatus(timeCell, it, flatId, true)
                return true
            }
        }
        return false
    }
}
