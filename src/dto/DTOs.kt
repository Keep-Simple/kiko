import org.jetbrains.annotations.NotNull

data class ReserveDto(val tenantId: Int, val flatId: Int, val timeCell: Int)
data class ReserveShortDto(val flatId: Int, val timeCell: Int)
