package com.theathletic.utility.datetime

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import com.theathletic.core.R
import com.theathletic.datetime.Chronos
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeProvider
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.datetime.formatter.DisplayFormat.DAY_OF_MONTH
import com.theathletic.datetime.formatter.DisplayFormat.HOURS_MINUTES
import com.theathletic.datetime.formatter.DisplayFormat.LOCALIZED_DATE
import com.theathletic.datetime.formatter.DisplayFormat.MONTH_DATE_LONG
import com.theathletic.datetime.formatter.DisplayFormat.MONTH_DATE_SHORT
import com.theathletic.datetime.formatter.DisplayFormat.MONTH_DATE_YEAR
import com.theathletic.datetime.formatter.DisplayFormat.MONTH_DATE_YEAR_SHORT
import com.theathletic.datetime.formatter.DisplayFormat.MONTH_SHORT
import com.theathletic.datetime.formatter.DisplayFormat.WEEKDAY_FULL
import com.theathletic.datetime.formatter.DisplayFormat.WEEKDAY_LONG_MONTH_LONG_DATE_LONG_YEAR
import com.theathletic.datetime.formatter.DisplayFormat.WEEKDAY_MONTH_DATE_ABBREVIATED
import com.theathletic.datetime.formatter.DisplayFormat.WEEKDAY_MONTH_DATE_SHORT
import com.theathletic.datetime.formatter.DisplayFormat.WEEKDAY_SHORT
import com.theathletic.datetime.formatter.DisplayFormat.WEEKDAY_SHORT_HOURS_MINUTES
import com.theathletic.datetime.formatter.DisplayFormat.WEEKDAY_SHORT_MONTH_DATE_LONG
import com.theathletic.datetime.formatter.DisplayFormat.YEAR_LONG
import com.theathletic.utility.logging.ICrashLogHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Suppress("unused")
object DateUtilityImpl : DateUtility, KoinComponent {
    private const val TIMEZONE_GMT = "GMT"
    private val timeProvider by inject<TimeProvider>()
    private val chronos by inject<Chronos>()
    private val crashLogHandler by inject<ICrashLogHandler>()
    private val context: Context by inject(named("application-context"))

    @Suppress("DEPRECATION")
    private val LOCALE: Locale = context.resources.configuration.locale

    private const val DAYS_IN_WEEK = 7

    private const val GMT_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val GMT_SHORT_FORMAT = "yyyy-MM-dd"
    private const val HOURS_MINUTES_SECONDS_FORMAT = "HH:mm:ss"

    @JvmStatic
    fun getCurrentTimeInGMT(): String {
        return formatDateToGMTString(timeProvider.currentDate)
    }

    @JvmStatic
    fun formatDateToGMTString(date: Date): String {
        return SimpleDateFormat(GMT_FORMAT, LOCALE).apply { timeZone = TimeZone.getTimeZone(TIMEZONE_GMT) }.format(date)
    }

    @JvmStatic
    fun formatGMTDateString(gmtDateString: String, format: DisplayFormat): String {
        val date = parseDateFromGMT(gmtDateString)
        return formatGMTDate(date, format)
    }

    @JvmStatic
    fun formatGMTDate(gmtDate: Date, format: DisplayFormat): String {
        return formatGMTDate(gmtDate.time, format)
    }

    override fun formatGMTDate(datetime: Datetime, format: DisplayFormat): String {
        return formatGMTDate(datetime.timeMillis, format)
    }

    @JvmStatic
    @Suppress("LongMethod")
    fun formatGMTDate(utcEpochMillis: Long, format: DisplayFormat): String {
        return when (format) {
            WEEKDAY_LONG_MONTH_LONG_DATE_LONG_YEAR -> {
                DateUtils.formatDateTime(
                    context, utcEpochMillis,
                    DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                )
            }
            WEEKDAY_MONTH_DATE_SHORT -> {
                DateUtils.formatDateTime(
                    context, utcEpochMillis,
                    DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_ALL or
                        DateUtils.FORMAT_NO_YEAR or DateUtils.FORMAT_SHOW_DATE
                )
            }
            WEEKDAY_MONTH_DATE_ABBREVIATED -> {
                val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEE Md")
                val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
                dateFormatter.format(Date(utcEpochMillis))
            }
            WEEKDAY_SHORT_MONTH_DATE_LONG -> {
                DateUtils.formatDateTime(
                    context, utcEpochMillis,
                    DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_WEEKDAY or
                        DateUtils.FORMAT_NO_YEAR or DateUtils.FORMAT_SHOW_DATE
                )
            }
            WEEKDAY_SHORT -> {
                DateUtils.formatDateTime(
                    context, utcEpochMillis,
                    DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_WEEKDAY or
                        DateUtils.FORMAT_NO_YEAR
                )
            }
            WEEKDAY_SHORT_HOURS_MINUTES -> {
                DateUtils.formatDateTime(
                    context, utcEpochMillis,
                    DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_WEEKDAY or
                        DateUtils.FORMAT_NO_YEAR or DateUtils.FORMAT_SHOW_TIME
                )
            }
            WEEKDAY_FULL -> {
                DateUtils.formatDateTime(
                    context, utcEpochMillis,
                    DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_NO_YEAR
                )
            }
            MONTH_DATE_YEAR -> {
                DateUtils.formatDateTime(context, utcEpochMillis, DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_SHOW_DATE)
            }
            MONTH_DATE_YEAR_SHORT -> {
                DateUtils.formatDateTime(
                    context, utcEpochMillis,
                    DateUtils.FORMAT_SHOW_YEAR or
                        DateUtils.FORMAT_ABBREV_ALL or DateUtils.FORMAT_SHOW_DATE
                )
            }
            MONTH_DATE_SHORT -> {
                DateUtils.formatDateTime(
                    context, utcEpochMillis,
                    DateUtils.FORMAT_NO_YEAR or
                        DateUtils.FORMAT_ABBREV_ALL or DateUtils.FORMAT_SHOW_DATE
                )
            }
            MONTH_SHORT -> {
                DateUtils.formatDateTime(
                    context, utcEpochMillis,
                    DateUtils.FORMAT_NO_YEAR or
                        DateUtils.FORMAT_ABBREV_ALL or DateUtils.FORMAT_NO_MONTH_DAY
                )
            }
            MONTH_DATE_LONG -> {
                DateUtils.formatDateTime(
                    context, utcEpochMillis,
                    DateUtils.FORMAT_NO_YEAR or
                        DateUtils.FORMAT_SHOW_DATE
                )
            }
            HOURS_MINUTES -> {
                DateUtils.formatDateTime(context, utcEpochMillis, DateUtils.FORMAT_SHOW_TIME)
            }
            DAY_OF_MONTH -> {
                // FORMAT_NUMERIC_DATE -> MM/DD just need the DD part
                getDayOfMonth(utcEpochMillis)
            }
            YEAR_LONG -> {
                val dateFormatter = SimpleDateFormat("yyyy", Locale.getDefault())
                dateFormatter.format(Date(utcEpochMillis))
            }
            LOCALIZED_DATE -> {
                val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "dd-MM-YY")
                val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
                dateFormatter.format(Date(utcEpochMillis))
            }
        }
    }

    private fun getDayOfMonth(utcEpochMillis: Long): String {
        val formattedDate = DateUtils.formatDateTime(context, utcEpochMillis, DateUtils.FORMAT_NUMERIC_DATE)
        val splits = formattedDate.split("/")
        return if (splits.size > 1) {
            splits[1]
        } else {
            crashLogHandler.trackException(
                IndexOutOfBoundsException(),
                "getDayOfMonth error with date format = $formattedDate and utcEpochMillis = $utcEpochMillis"
            )
            ""
        }
    }

    @JvmStatic
    fun formatCountdownDate(date: Date): String {
        return SimpleDateFormat(HOURS_MINUTES_SECONDS_FORMAT, LOCALE).apply {
            timeZone = TimeZone.getTimeZone(
                TIMEZONE_GMT
            )
        }.format(date.time)
    }

    @JvmStatic
    fun formatGiftsCalendar(calendar: Calendar?): String? {
        calendar?.let {
            return SimpleDateFormat("yyyy-MM-dd", LOCALE).format(it.time)
        }
        return null
    }

    @JvmStatic
    fun formatScoreGameTimeGMTToLocalizedGameTime(date: String?): String {
        date?.let {
            return SimpleDateFormat("yyyy-MM-dd", LOCALE).format(parseDateFromGMT(it))
        }
        return ""
    }

    @JvmStatic
    fun formatPodcastDate(date: String?): String {
        date?.let {
            val milliseconds = parseDateFromGMT(date).time
            val millisecondsYesterday = milliseconds + DateUtils.DAY_IN_MILLIS // Add one day to check for yesterday

            return when {
                DateUtils.isToday(milliseconds) -> context.getString(R.string.global_date_today)
                DateUtils.isToday(millisecondsYesterday) -> context.getString(R.string.global_date_yesterday)
                else -> formatGMTDate(milliseconds, WEEKDAY_MONTH_DATE_SHORT)
            }
        }
        return ""
    }

    @JvmStatic
    fun formatGiftsDeliveryDate(timeStamp: Long): String {
        return formatGMTDate(timeStamp, WEEKDAY_LONG_MONTH_LONG_DATE_LONG_YEAR)
    }

    @JvmStatic
    fun formatCommunityLiveDiscussionsDate(startTimeGmt: String, endTimeGmt: String): String {
        // Starts - mon, jul 21 / Tomorrow / Today - 12:30am - 1:30pm
        // Live - Ends in 33m
        // Ended - Today / Yesterday / mon, jul 21

        val date = timeProvider.currentDate
        val startTimeRemaining = parseDateFromGMT(startTimeGmt).time - date.time
        val endTimeRemaining = parseDateFromGMT(endTimeGmt).time - date.time

        return when {
            startTimeRemaining > 0 -> {
                // Tt Starts at
                val milliseconds = parseDateFromGMT(startTimeGmt).time
                val millisecondsTomorrow = milliseconds - DateUtils.DAY_IN_MILLIS // Sub one day to check for tomorrow

                when {
                    DateUtils.isToday(milliseconds) -> context.getString(R.string.global_date_today_time_span, formatGMTDateString(startTimeGmt, HOURS_MINUTES), formatGMTDateString(endTimeGmt, HOURS_MINUTES))
                    DateUtils.isToday(millisecondsTomorrow) -> context.getString(R.string.global_date_tomorrow_time_span, formatGMTDateString(startTimeGmt, HOURS_MINUTES), formatGMTDateString(endTimeGmt, HOURS_MINUTES))
                    else -> formatGMTDateString(startTimeGmt, WEEKDAY_MONTH_DATE_SHORT)
                }
            }
            endTimeRemaining <= 0 -> {
                // Tt Ended
                formatTimeAgoFromGMT(endTimeGmt, false)
            }
            else -> {
                // Tt Live
                formatCommunityLiveDiscussionsEndsInDate(endTimeGmt)
            }
        }
    }

    @JvmStatic
    fun formatCommunityLiveDiscussionsDateV2(startTimeGmt: String, endTimeGmt: String): String {
        // Starts - mon, jul 21 / Tomorrow / Today - 12:30am - 1:30pm
        // Live - Ends in 33m
        // Ended - Today / Yesterday / mon, jul 21

        val date = timeProvider.currentDate

        val endDate = parseDateFromGMT(endTimeGmt)
        val endTimeRemaining = endDate.time - date.time

        return when {
            endTimeRemaining > 0 -> {
                // Tt Starts at & Live
                val milliseconds = parseDateFromGMT(startTimeGmt).time

                when {
                    DateUtils.isToday(milliseconds) -> context.getString(
                        R.string.global_date_today_time_span,
                        formatGMTDateString(startTimeGmt, HOURS_MINUTES),
                        formatGMTDateString(endTimeGmt, HOURS_MINUTES)
                    )
                    endDate.isWithinWeek() -> context.getString(
                        R.string.global_date_day_of_week_time_span,
                        formatGMTDateString(startTimeGmt, WEEKDAY_SHORT),
                        formatGMTDateString(startTimeGmt, HOURS_MINUTES).stripEmptyMinutes(),
                        formatGMTDateString(endTimeGmt, HOURS_MINUTES).stripEmptyMinutes()
                    )
                    else -> context.getString(
                        R.string.global_date_day_of_week_time_span,
                        formatGMTDateString(startTimeGmt, MONTH_DATE_SHORT),
                        formatGMTDateString(startTimeGmt, HOURS_MINUTES).stripEmptyMinutes(),
                        formatGMTDateString(endTimeGmt, HOURS_MINUTES).stripEmptyMinutes()
                    )
                }
            }
            else -> {
                // Tt Ended
                formatTimeAgoFromGMT(endTimeGmt, false)
            }
        }
    }

    fun Date.wasInLastWeek(): Boolean {
        val dateOneWeekAgo = Calendar.getInstance().run {
            time = timeProvider.currentDate
            add(Calendar.DATE, -DAYS_IN_WEEK)
            time
        }
        return dateOneWeekAgo.before(this)
    }

    fun Date.isWithinWeek(): Boolean {
        val dateInOneWeek = Calendar.getInstance().run {
            time = timeProvider.currentDate
            add(Calendar.DATE, DAYS_IN_WEEK)
            time
        }
        return dateInOneWeek.after(this)
    }

    private fun String.stripEmptyMinutes(): String {
        return if (this.contains(":00")) {
            replace(":00", "")
        } else {
            this
        }
    }

    @SuppressLint("StringFormatMatches")
    @JvmStatic
    fun formatCommunityLiveDiscussionsEndsInDate(endTimeGmt: String): String {
        // Live - Ends in 33m
        val date = timeProvider.currentDate
        val endTimeRemainingSec = (parseDateFromGMT(endTimeGmt).time - date.time) / 1000
        return if (endTimeRemainingSec / 60 > 60)
            context.getString(R.string.community_topic_live_ends_in_h, endTimeRemainingSec / 60 / 60)
        else
            context.getString(R.string.community_topic_live_ends_in_m, (endTimeRemainingSec / 60) + 1)
    }

    @SuppressLint("StringFormatMatches")
    @JvmStatic
    fun formatFeedLiveDiscussionsEndsInDate(endTimeGmt: String): String {
        // Live - 33 Min left
        val date = timeProvider.currentDate
        val endTimeRemaining = parseDateFromGMT(endTimeGmt).time - date.time
        return context.getString(R.string.fragment_feed_item_live_discussions_ends_in, (endTimeRemaining / 60000) + 1)
    }

    fun formatPodcastDurationHHmmss(timeMs: Long): String {
        val h = TimeUnit.MILLISECONDS.toHours(timeMs)
        val m = TimeUnit.MILLISECONDS.toMinutes(timeMs) % TimeUnit.HOURS.toMinutes(1)
        val s = TimeUnit.MILLISECONDS.toSeconds(timeMs) % TimeUnit.MINUTES.toSeconds(1)
        return when {
            h > 0 -> String.format("%02d:%02d:%02d", h, m, s)
            else -> String.format("%02d:%02d", m, s)
        }
    }

    @JvmStatic
    fun formatPodcastDuration(timeMillis: Long): String {
        val duration = timeMillis + TimeUnit.MINUTES.toMillis(1)
        val minString = context.getString(R.string.global_time_min)
        val hrString = context.getString(R.string.global_time_hr)
        return when {
            duration / DateUtils.HOUR_IN_MILLIS == 0L -> SimpleDateFormat("m' $minString'", LOCALE)
            else -> SimpleDateFormat("H' $hrString' mm' $minString'", LOCALE)
        }.apply { timeZone = TimeZone.getTimeZone(TIMEZONE_GMT) }.format(Date(duration))
    }

    @JvmStatic
    fun formatPodcastTrackDuration(timeMillis: Long): String {
        val oneMinute = TimeUnit.MINUTES.toMillis(1)
        val duration = if (timeMillis < oneMinute) oneMinute else timeMillis
        val minString = context.getString(R.string.global_time_min).uppercase(LOCALE)
        return "${duration / TimeUnit.MINUTES.toMillis(1)} $minString"
    }

    @JvmStatic
    fun formatPodcastTrackTimeSpan(startMillis: Long, endMillis: Long): String {
        val (startValue, endValue) = cleanPodcastStartEndTimes(startMillis, endMillis)

        fun formatTimeString(timeMillis: Long) = when {
            timeMillis / DateUtils.HOUR_IN_MILLIS == 0L -> SimpleDateFormat("m:ss", LOCALE)
            else -> SimpleDateFormat("h:mm:ss", LOCALE)
        }.apply { timeZone = TimeZone.getTimeZone(TIMEZONE_GMT) }.format(Date(timeMillis))

        return "${formatTimeString(startValue)}-${formatTimeString(endValue)}"
    }

    private fun cleanPodcastStartEndTimes(startMillis: Long, endMillis: Long): Pair<Long, Long> {
        var startValue = startMillis
        var endValue = endMillis

        if (startValue < 0)
            startValue = 0

        if (endValue < 0)
            endValue = 0

        if (startValue > endValue)
            startValue = endValue.also { endValue = startValue }

        return Pair(startValue, endValue)
    }

    @JvmStatic
    fun formatPodcastTimeRemaining(timeMillis: Long): String {
        val timeLeft = timeMillis + TimeUnit.MINUTES.toMillis(1)
        val minRemainingString = context.getString(R.string.podcast_time_min_left)
        val hrString = context.getString(R.string.global_time_hr)
        return when {
            timeLeft / DateUtils.HOUR_IN_MILLIS == 0L -> SimpleDateFormat("m' $minRemainingString'", LOCALE)
            else -> SimpleDateFormat("H' $hrString' mm' $minRemainingString'", LOCALE)
        }.apply { timeZone = TimeZone.getTimeZone(TIMEZONE_GMT) }.format(Date(timeLeft))
    }

    override fun formatGMTTimeAgo(
        dateString: String,
        includeNowTag: Boolean,
        short: Boolean
    ): String {
        return formatTimeAgoFromGMT(dateString, includeNowTag, short)
    }

    override fun formatGMTTimeAgo(
        date: Date,
        includeNowTag: Boolean,
        short: Boolean
    ): String {
        return formatTimeAgoFromGMT(date, includeNowTag, short)
    }

    @JvmStatic
    @JvmOverloads
    fun formatTimeAgoFromGMT(
        dateString: String?,
        includeNowTag: Boolean = true,
        short: Boolean = false
    ): String {
        if (dateString == null) return ""

        return formatTimeAgoFromGMT(
            parseDateFromGMT(dateString),
            includeNowTag,
            short
        )
    }

    @JvmStatic
    fun formatTimeAgoFromGMT(
        date: Date,
        includeNowTag: Boolean = true,
        short: Boolean = false
    ): String {
        val milliseconds = date.time
        val millisecondsYesterday = milliseconds + DateUtils.DAY_IN_MILLIS // Add one day to check for today
        val currentMilliseconds = timeProvider.currentTimeMs
        val differenceInSeconds = (currentMilliseconds - milliseconds) / 1000
        val differenceInMinutes = (differenceInSeconds / 60).toInt()
        val differenceInHours = differenceInMinutes / 60
        val differenceInDays = (differenceInHours / 24.0f).roundToInt()

        return if (short) {
            when {
                differenceInSeconds < TimeUnit.MINUTES.toSeconds(2) && includeNowTag ->
                    context.getString(R.string.plural_time_now)
                differenceInSeconds < TimeUnit.HOURS.toSeconds(1) ->
                    context.getString(R.string.time_minutes_ago, differenceInMinutes)
                differenceInSeconds < TimeUnit.HOURS.toSeconds(24) ->
                    context.getString(R.string.time_hours_ago, differenceInHours)
                date.wasInLastWeek() ->
                    context.getString(R.string.time_days_ago, differenceInDays)

                DateUtils.isToday(milliseconds) ->
                    context.getString(R.string.global_date_earlier_today)
                DateUtils.isToday(millisecondsYesterday) ->
                    context.getString(R.string.global_date_yesterday)
                isThisYear(milliseconds) -> formatGMTDate(milliseconds, WEEKDAY_MONTH_DATE_SHORT)
                else -> formatGMTDate(milliseconds, MONTH_DATE_YEAR_SHORT)
            }
        } else {
            when {
                differenceInSeconds < TimeUnit.MINUTES.toSeconds(2) && includeNowTag ->
                    context.getString(R.string.plural_time_now)
                differenceInSeconds < TimeUnit.HOURS.toSeconds(1) ->
                    context.resources.getQuantityString(R.plurals.plural_time_minutes_ago, differenceInMinutes, differenceInMinutes)
                differenceInSeconds < TimeUnit.HOURS.toSeconds(12) ->
                    context.resources.getQuantityString(R.plurals.plural_time_hours_ago, differenceInHours, differenceInHours)
                DateUtils.isToday(milliseconds) ->
                    context.getString(R.string.global_date_earlier_today)
                DateUtils.isToday(millisecondsYesterday) ->
                    context.getString(R.string.global_date_yesterday)
                isThisYear(milliseconds) -> formatGMTDate(milliseconds, WEEKDAY_MONTH_DATE_SHORT)
                else -> formatGMTDate(milliseconds, MONTH_DATE_YEAR_SHORT)
            }
        }
    }

    @JvmStatic
    fun isDateTodayFromGMT(date: String?): Boolean = if (date != null) DateUtils.isToday(parseDateFromGMT(date).time) else false

    @JvmStatic
    fun isInPastMoreThanFromGMT(date: String?, milliseconds: Long): Boolean {
        date?.let {
            val millisecondsCurrent = timeProvider.currentTimeMs - milliseconds // Remove X milliseconds
            val millisecondsCheck = parseDateFromGMT(date).time
            return millisecondsCurrent > millisecondsCheck
        }
        return true
    }

    @JvmStatic
    fun isYesterdayOrNewer(date: String?): Boolean {
        date?.let {
            val milliseconds = parseDateFromGMT(date).time
            val millisecondsYesterday = milliseconds + DateUtils.DAY_IN_MILLIS // Add one day to check for yesterday
            return DateUtils.isToday(milliseconds) || DateUtils.isToday(millisecondsYesterday)
        }
        return true
    }

    override fun isInFutureMoreThan(date: String?, milliseconds: Long): Boolean {
        return isInFutureMoreThan(parseDateFromGMT(date), milliseconds)
    }

    override fun isInFutureMoreThan(date: Date?, milliseconds: Long): Boolean {
        date?.let {
            val millisecondsCurrent = timeProvider.currentTimeMs
            val millisecondsCheck = date.time - milliseconds // Remove X milliseconds
            return millisecondsCurrent < millisecondsCheck
        }
        return true
    }

    override fun isInPastMoreThan(timeStamp: Long, milliseconds: Long): Boolean {
        val timeSinceRefresh = timeProvider.currentTimeMs - timeStamp
        return timeSinceRefresh > milliseconds
    }

    override fun parseDateFromGMT(date: String?): Date {
        date?.let {
            try {
                val originalFormat = SimpleDateFormat(GMT_FORMAT, LOCALE)
                originalFormat.timeZone = TimeZone.getTimeZone(TIMEZONE_GMT)
                originalFormat.parse(it)?.let { parsedDate ->
                    return parsedDate
                }
            } catch (ignored: Exception) {
            }
            try {
                val originalFormat = SimpleDateFormat(GMT_SHORT_FORMAT, LOCALE)
                originalFormat.timeZone = TimeZone.getTimeZone(TIMEZONE_GMT)
                originalFormat.parse(it)?.let { parsedDate ->
                    return parsedDate
                }
            } catch (ignored: Exception) {
            }
        }
        // This is probably the most easy and stable way we can handle the time error now.
        // Returning null would be probably better, but it looks like it's already too late to do so.
        return timeProvider.currentDate.apply { time = 0 }
    }

    @JvmStatic
    fun formatScoresDayAndDate(date: String?): String {
        return date?.let {
            val df = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, Locale.getDefault())
            val shortDate = df.format(parseDateFromGMT(it))
                .replaceAfterLast("/", "").trimEnd('/')
            "${formatGMTDateString(it, WEEKDAY_SHORT)} $shortDate"
        } ?: ""
    }

    override fun getCurrentLocalDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", LOCALE).format(timeProvider.currentDate)
    }

    override fun isThisYear(milliseconds: Long) = chronos.isThisYear(milliseconds)

    override fun getStartOfDay() = Datetime(
        Calendar.getInstance(LOCALE).apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 1)
        }.timeInMillis
    )
}