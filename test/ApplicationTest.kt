package com.kiko

import ReserveDto
import ReserveShortDto
import TimeSlot
import com.kiko.models.Flat
import com.kiko.services.NotificationService
import com.kiko.services.ViewSchedulerService
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testReserveRejection() {
        Flat(1).run {
            val timeSlot = TimeSlot(0, 4)
            reserveView(timeSlot, 28)
            assertEquals(schedule.get(timeSlot)?.tenantId, 28)

            rejectView(timeSlot)
            assertEquals(schedule.get(timeSlot)?.banned, true)

            assertFalse(reserveView(timeSlot, 42))
        }
    }

    @Test
    fun testConcurrentReservation() {
        val flat = Flat(3)
        val timeSlot = TimeSlot(1, 1)
        runBlocking {
            val firstRes = async { flat.reserveView(timeSlot, 1) }
            val secondRes = async { flat.reserveView(timeSlot, 2) }
            assertTrue(firstRes.await())
            assertFalse(secondRes.await())
        }
    }

    @Test
    fun testReserveCancellation() {
        val timeSlot = TimeSlot(0, 0)
        Flat(2).run {
            reserveView(timeSlot, 48)
            approveView(timeSlot)
            cancelView(timeSlot)
            assertNull(schedule.get(timeSlot)?.tenantId)

            reserveView(timeSlot, 98)
            assertEquals(schedule.get(timeSlot)?.tenantId, 98)
        }
    }

    @Test
    fun testServiceReserve() {
        val notificationService = NotificationService()
        val flats = mapOf(1 to Flat(1))
        val service = ViewSchedulerService(flats, notificationService)
        val timeSlot = TimeSlot(0, 1)

        service.run {
            // current tenant shouldn't block view slots
            assertFalse(reserve(ReserveDto(1, timeSlot, 1)))

            val secondTenant = ReserveDto(1, timeSlot, 31)
            assertTrue(reserve(secondTenant))

            // this house is reserved
            val thirdTenant = ReserveDto(1, timeSlot, 41)
            assertFalse(reserve(thirdTenant))

            cancelReservation(ReserveShortDto(1, timeSlot))
            assertTrue(reserve(thirdTenant))
        }
    }
}
