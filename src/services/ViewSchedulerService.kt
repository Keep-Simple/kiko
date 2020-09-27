package com.kiko.services

import ReserveDto
import ReserveShortDto
import com.kiko.models.Flat

class ViewSchedulerService(
    private val flats: Map<Int, Flat>,
    private val notificationService: NotificationService
) {
    fun reserve(dto: ReserveDto): Boolean {
        val (flatId, timeSlot, tenantId) = dto
        flats[flatId]
            ?.takeIf { tenantId != it.currentTenantId }
            ?.run {
                if (reserveView(timeSlot, tenantId)) {
                    notificationService.notifyTenantOfReservation(timeSlot, tenantId, flatId)
                    return true
                }
            }
        return false
    }

    fun cancelReservation(dto: ReserveShortDto) {
        val (flatId, timeSlot) = dto
        flats[flatId]?.run {
            cancelView(timeSlot)
            notificationService.notifyTenantOfCancellation(timeSlot, flatId, currentTenantId)
        }
    }

    fun rejectReservation(dto: ReserveShortDto): Boolean {
        val (flatId, timeSlot) = dto
        flats[flatId]?.run {
            rejectView(timeSlot)?.let {
                notificationService.notifyNewTenantOfViewStatus(timeSlot, it, flatId, false)
                return true
            }
        }
        return false
    }

    fun approveReservation(dto: ReserveShortDto): Boolean {
        val (flatId, timeSlot) = dto
        flats[flatId]?.run {
            approveView(timeSlot)?.let {
                notificationService.notifyNewTenantOfViewStatus(timeSlot, it, flatId, true)
                return true
            }
        }
        return false
    }
}
