package com.kiko

import ReserveDto
import ReserveShortDto
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
            reserveView(4, 28)
            assertEquals(viewSchedule[4].tenantId, 28)

            rejectView(4)
            assertTrue(viewSchedule[4].banned)

            assertFalse(reserveView(4, 42))
        }
    }

    @Test
    fun testReserveCancellation() {
        Flat(2).run {
            reserveView(0, 48)
            approveView(0)
            cancelView(0)
            assertNull(viewSchedule[0].tenantId)

            reserveView(0, 98)
            assertEquals(viewSchedule[0].tenantId, 98)
        }
    }

    @Test
    fun testConcurrentReservation() {
        val flat = Flat(3)
        runBlocking {
            val firstRes = async { flat.reserveView(8, 1) }
            val secondRes = async { flat.reserveView(8, 2) }
            assertTrue(firstRes.await())
            assertFalse(secondRes.await())
        }
    }

    @Test
    fun testServiceReserve() {
        val notificationService = NotificationService()
        val flats = mutableMapOf(1 to Flat(1), 2 to Flat(2), 3 to Flat(2))
        val service = ViewSchedulerService(flats, notificationService)

        service.run {
            assertFalse(reserve(ReserveDto(1, 1, 0)))

            val secondTenant = ReserveDto(2, 1, 31)
            assertTrue(reserve(secondTenant))

            val thirdTenant = ReserveDto(3, 1, 31)
            assertFalse(reserve(thirdTenant))

            cancelReservation(ReserveShortDto(1, 31))
            assertTrue(reserve(thirdTenant))
        }
    }
}
