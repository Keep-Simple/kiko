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
        flats[flatId].let {
            if (it != null && tenantId != it.currentTenantId && it.reserveView(timeCell, tenantId)) {
                notificationService.notifyTenantOfReservation(timeCell, tenantId, flatId)
                return true;
            }
            return false;
        }
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
                rejectView(timeCell)?.let {
                    notificationService.notifyNewTenantOfViewStatus(timeCell, it, flatId, false)
                }
            }
        }

        fun approveReservation(dto: ReserveShortDto) {
            val (flatId, timeCell) = dto
            flats[flatId]?.run {
                approveView(timeCell)?.let {
                    notificationService.notifyNewTenantOfViewStatus(timeCell, it, flatId, true)
                }
            }
        }
    }
