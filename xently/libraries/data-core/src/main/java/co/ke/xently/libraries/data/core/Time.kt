package co.ke.xently.libraries.data.core

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Time.TimeAsStringSerializer::class)
data class Time(
    val hour: Int,
    val minute: Int,
    val utcOffset: UtcOffset = TimeZone.currentSystemDefault()
        .offsetAt(Clock.System.now()),
) {
    fun toLocalTime(): Time {
        val (date, time) = Clock.System.now()
            .toString()
            .replace("T\\d+:\\d+".toRegex(), "T${toString(true)}")
            .split('T')

        val cleansedTime = time.replace("[Z+-]+".toRegex(), "")
            .plus(utcOffset.toString())

        return Instant.parse("${date}T${cleansedTime}")
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .let {
                Time(
                    hour = it.hour,
                    minute = it.minute,
                )
            }
    }

    fun toString(is24hour: Boolean): String {
        return buildString {
            if (is24hour && hour < 10) {
                append("0")
            }
            if (is24hour || hour <= 12) {
                append(hour)
            } else {
                append(hour - 12)
            }
            append(':')
            if (minute < 10) {
                append("0")
            }
            append(minute)
            if (!is24hour) {
                append(' ')
                if (hour >= 12) {
                    append("pm")
                } else {
                    append("am")
                }
            }
        }
    }

    object TimeAsStringSerializer : KSerializer<Time> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Time", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Time) {
            val string = buildString {
                if (value.hour < 10) {
                    append('0')
                }
                append(value.hour)
                append(':')
                if (value.minute < 10) {
                    append('0')
                }
                append(value.minute)
                append(' ')
                value.utcOffset.toString()
                    .replace("Z", "+0000")
                    .replace(":", "")
                    .also(::append)
            }
            encoder.encodeString(string)
        }

        override fun deserialize(decoder: Decoder): Time {
            val offsetTime = decoder.decodeString()
            val (time, offset) = offsetTime.split(' ')
            val (hour, minute) = time.split(':').map {
                it.toInt()
            }
            val utcOffset = UtcOffset.parse(offset, UtcOffset.Formats.FOUR_DIGITS)
            return Time(hour = hour, minute = minute, utcOffset = utcOffset)
                .toLocalTime()
        }
    }
}