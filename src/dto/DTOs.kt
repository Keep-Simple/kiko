data class ReserveDto(
    val flatId: Int,
    val timeSlot: TimeSlot,
    val tenantId: Int
)

data class ReserveShortDto(
    val flatId: Int,
    val timeSlot: TimeSlot
)

data class TimeSlot(
    val day: Byte,
    val slot: Byte
)
