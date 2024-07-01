import Foundation

extension Date {

    public struct Constants {
        public static let dayMonthUsFormat = "M/d"
        public static let dayMonthNonUsFormat = "d/M"
        public static let monthDayFormat = "MMMM, d"
        public static let monthDayNoCommaFormat = "MMMM d"
        public static let monthDayYearFormat = "MMMM d, yyyy"
        public static let weekdayMonthDayFormat = "EEEE, MMM d"
        public static let shortWeekdayMonthDayFormat = "EEE, MMMM d"
        public static let shortWeekdayShortMonthDayFormat = "EEE, MMM d"
        public static let shortMonthFormat = "MMM"
        public static let shortMonthDayFormat = "MMM d"
        public static let shortMonthDayYearFormat = "MMM d, yyyy"
        public static let shortDayFormat = "EEE"
        public static let longDayFormat = "EEEE"
        public static let yearFormat = "yyyy"
        public static let iso8601MillisecondsFormat = "yyyy-MM-dd HH:mm:ss.SSS"
        public static let iso8601Format = "yyyy-MM-dd HH:mm:ss"
        public static let iso8601ShortFormat = "MM-dd-yyyy"
        public static let communityFormat = "h:mma"
        public static let timeWithSpaceFormat = "h:mm a"
        public static let communityStartFormat = "MMM d, ha"
        public static let communityEndFormat = "ha"
        public static let apiFormat = "yyyy'-'MM'-'dd' 'HH':'mm':'ss"
        public static let shortDateFormat = "yyyy-MM-dd"
        public static let futureLiveDiscussionFormat = "EEE, h:mma"
        public static let giftDateFormat1 = "EEEE, MMMM dd, yyyy"
        public static let giftDateFormat2 = "EEEE, dd MMMM, yyyy"
        public static let boxScoreLastGamesFormat = "MMM d, yyyy"
        public static let liveRoomCreatedAtFormat = "EEE MMM d, y at h:mm a"
        public static let podcastEpisodeDateFormat = "E, d MMMM, yyyy"
    }

    // MARK: - Conditions

    public var day: Int {
        return Calendar.current.component(.day, from: self)
    }

    public var month: Int {
        return Calendar.current.component(.month, from: self)
    }

    public var year: Int {
        return Calendar.current.component(.year, from: self)
    }

    public var isDistantPast: Bool {
        Calendar.current.component(.year, from: self) < 2010
    }

    public var isToday: Bool {
        return isSame(Date(), granularity: .day)
    }

    public var millisecondsSince1970: Int {
        return Int(timeIntervalSince1970 * 1000.0)
    }

    public func isSame(
        _ date: Date,
        granularity: Calendar.Component? = nil,
        timeSettings: TimeSettings = SystemTimeSettings()
    ) -> Bool {
        if let granularity = granularity {
            return timeSettings.calendar.compare(
                self,
                to: date,
                toGranularity: granularity
            ) == .orderedSame
        } else {
            return compare(date) == .orderedSame
        }
    }

    public func isOlderThan(
        _ timeInterval: TimeInterval,
        timeSettings: TimeSettings = SystemTimeSettings()
    ) -> Bool {
        return timeIntervalSince(timeSettings.now()) < -timeInterval
    }

    public func isNewerThan(
        _ timeInterval: TimeInterval,
        timeSettings: TimeSettings = SystemTimeSettings()
    ) -> Bool {
        return timeSettings.now().timeIntervalSince(self) < timeInterval
    }

    public var isFuture: Bool {
        timeIntervalSinceNow.sign == .plus
    }

    public var isPast: Bool {
        timeIntervalSinceNow.sign == .minus
    }

    public func startOfDay(for timeZone: TimeZone, in calendar: Calendar = .current) -> Date {
        var timeZoneDependentCalendar = (calendar as NSCalendar).copy() as! Calendar

        if timeZoneDependentCalendar.timeZone != timeZone {
            timeZoneDependentCalendar.timeZone = timeZone
        }

        return timeZoneDependentCalendar.startOfDay(for: self)
    }

    public func timeAgo(
        component: Calendar.Component,
        timeSettings: TimeSettings = SystemTimeSettings()
    ) -> Int? {
        return timeSettings.calendar.dateComponents(
            [component],
            from: self,
            to: timeSettings.now()
        ).value(for: component)
    }

    public func timeShort(timeSettings: TimeSettings = SystemTimeSettings()) -> String {
        guard !isDistantPast else { return "" }
        guard isOlderThan(1.minute, timeSettings: timeSettings) else {
            return Strings.justNow.localized
        }

        return Date.abbreviatedComponentsFormatter.string(
            from: -timeIntervalSince(timeSettings.now())
        ) ?? ""
    }

    /// A short string representing the date in human readable form.
    /// If the date is today, returns "Today".
    /// Dates within the upcoming week return the shorthand day of the week.
    /// Dates in the past or beyond one week return the shorthand month name and day number.
    ///
    /// - Parameter timeSettings: Time environment settings
    /// - Returns: Human readable date/day string
    public func dateShort(timeSettings: TimeSettings = SystemTimeSettings()) -> String {
        if isSame(timeSettings.now(), granularity: .day) {
            return Strings.today.localized.capitalized
        } else {
            let startOfToday = timeSettings.now().startOfDay(for: timeSettings.timeZone)
            let startOfTomorrow = startOfToday.add(days: 1, calendar: timeSettings.calendar)

            let isWithinUpcomingWeek =
                self >= startOfTomorrow
                && self < startOfToday.add(days: 7, calendar: timeSettings.calendar)

            if isWithinUpcomingWeek {
                return Date.shortDayFormatter.string(from: self)
            } else {
                return Date.shortMonthDayFormatter.string(from: self)
            }
        }
    }

    public func timeAgoLessThanWeek(timeSettings: TimeSettings = SystemTimeSettings()) -> String {
        guard let days = timeAgo(component: .day, timeSettings: timeSettings), days < 7 else {
            if isSame(timeSettings.now(), granularity: .year) {
                return Date.monthDayFormatter.string(from: self)
            } else {
                return Date.monthDayYearFormatter.string(from: self)
            }
        }

        return timeAgoShort(timeSettings: timeSettings)
    }

    public func timeAgoShort(timeSettings: TimeSettings = SystemTimeSettings()) -> String {

        // If we have a date in the distant past, we shouldn't generate a time ago
        // instead, show no date.
        guard
            !isDistantPast,
            let timeString = Date.abbreviatedComponentsFormatter.string(
                from: -timeIntervalSince(timeSettings.now())
            )
        else {
            return ""
        }

        guard isOlderThan(1.minute) else { return Strings.justNow.localized }

        return String(
            format: NSLocalizedString("%@ ago", comment: ""),
            locale: Locale.current,
            timeString
        )
    }

    public func timeLessThanWeek(timeSettings: TimeSettings = SystemTimeSettings()) -> String {
        guard let days = timeAgo(component: .day, timeSettings: timeSettings), days < 7 else {
            if isSame(timeSettings.now(), granularity: .year) {
                return Date.monthDayFormatter.string(from: self)
            } else {
                return Date.monthDayYearFormatter.string(from: self)
            }
        }

        return timeShort(timeSettings: timeSettings)
    }

    public var dayOfWeekDayMonthFormatted: String {
        let dayOfWeekString = Date.shortDayFormatter.string(from: self)
        let monthDayString = Date.localizedDayMonthFormatter.string(from: self)

        return dayOfWeekString + ", " + monthDayString
    }

    public static let localizedDayMonthFormatter: DateFormatter = {
        let format =
            Locale.current.identifier == "en_US" || Locale.current.identifier == "en_US_POSIX"
            ? Constants.dayMonthUsFormat
            : Constants.dayMonthNonUsFormat

        return dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: format)
        ) as DateFormatter
    }()

    // MARK: - Operations

    public func add(seconds: Int, calendar: Calendar = .current) -> Date {
        return calendar.date(byAdding: .second, value: seconds, to: self)!
    }

    public func add(minutes: Int, calendar: Calendar = .current) -> Date {
        return calendar.date(byAdding: .minute, value: minutes, to: self)!
    }

    public func add(hours: Int, calendar: Calendar = .current) -> Date {
        return calendar.date(byAdding: .hour, value: hours, to: self)!
    }

    public func add(days: Int, calendar: Calendar = .current) -> Date {
        return calendar.date(byAdding: .day, value: days, to: self)!
    }

    public func add(months: Int, calendar: Calendar = .current) -> Date {
        return calendar.date(byAdding: .month, value: months, to: self)!
    }

    public func add(years: Int, calendar: Calendar = .current) -> Date {
        return calendar.date(byAdding: .year, value: years, to: self)!
    }

    // MARK: - Display

    public static func secondsToHoursAndMinutes(_ seconds: Int) -> String {
        let (hour, minute) = (seconds / 3600, (seconds % 3600) / 60)
        return hour == 0 ? "\(minute)m" : "\(hour)h \(minute)m"
    }

    public static func secondsToHoursAndMinutesLong(_ seconds: Int) -> String {
        let (hour, minute) = (seconds / 3600, (seconds % 3600) / 60)
        return hour == 0 ? "\(minute) min" : "\(hour) hr \(minute) min"
    }

    public static func secondsToHoursMinutesAndSecondsShort(
        _ seconds: Int,
        alwaysShowHours: Bool = true
    ) -> String {
        let (hour, minute, second) = (seconds / 3600, (seconds % 3600) / 60, seconds % 60)
        if !alwaysShowHours && hour == 0 {
            return "\(twoDigitInt(for: minute)):\(twoDigitInt(for: second))"
        } else {
            return
                "\(twoDigitInt(for: hour)):\(twoDigitInt(for: minute)):\(twoDigitInt(for: second))"
        }
    }

    private static func twoDigitInt(for int: Int) -> String {
        String(format: "%02d", int)
    }

    // MARK: - Formatters

    public static var monthDayFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: Constants.monthDayFormat)
        ) as DateFormatter
    }()

    public static var monthDayYearFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: Constants.monthDayYearFormat)
        ) as DateFormatter
    }()

    public static var monthDayNoCommaFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: Constants.monthDayNoCommaFormat)
        ) as DateFormatter
    }()

    public static var shortMonthFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: Constants.shortMonthFormat)
        ) as DateFormatter
    }()

    public static var shortMonthDayFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: Constants.shortMonthDayFormat)
        ) as DateFormatter
    }()

    public static var shortMonthDayYearFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(
                template: Constants.shortMonthDayYearFormat
            )
        ) as DateFormatter
    }()

    public static var shortDayFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: Constants.shortDayFormat)
        ) as DateFormatter
    }()

    public static var longDayFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: Constants.longDayFormat)
        ) as DateFormatter
    }()

    public static var shortWeekdayMonthDayFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(
                template: Constants.shortWeekdayMonthDayFormat
            )
        ) as DateFormatter
    }()

    public static var shortWeekdayShortMonthDayFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(
                template: Constants.shortWeekdayShortMonthDayFormat
            )
        ) as DateFormatter
    }()

    public static var yearFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: Constants.yearFormat)
        ) as DateFormatter
    }()

    public static var iso8601MillisecondsFormatter: DateFormatter = {
        dateFormatter(
            forFormat: Constants.iso8601MillisecondsFormat
        ) as DateFormatter
    }()

    public static var wordpressDateFormatter: DateFormatter = {
        let formatter = dateFormatter(forFormat: Constants.iso8601Format) as DateFormatter
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        formatter.locale = Locale(identifier: "en_US_POSIX")

        return formatter
    }()

    public static var communityFormatter: DateFormatter = {
        dateFormatter(forFormat: Constants.communityFormat) as DateFormatter
    }()

    public static var timeWithSpaceFormatter: DateFormatter = {
        dateFormatter(forFormat: Constants.timeWithSpaceFormat) as DateFormatter
    }()

    public static var communityStartFormatter: DateFormatter = {
        dateFormatter(forFormat: Constants.communityStartFormat) as DateFormatter
    }()

    public static var communityEndFormatter: DateFormatter = {
        dateFormatter(forFormat: Constants.communityEndFormat) as DateFormatter
    }()

    public static var apiFormatter: DateFormatter = {
        let formatter = localApiFormatter
        formatter.timeZone = TimeZone(abbreviation: "UTC")

        return formatter
    }()

    public static var gmtApiFormatter: DateFormatter = {
        let formatter = dateFormatter(forFormat: Constants.iso8601Format) as DateFormatter
        formatter.timeZone = TimeZone(abbreviation: "GMT")
        formatter.locale = Locale(identifier: "en_US_POSIX")

        return formatter
    }()

    public static var localApiFormatter: DateFormatter = {
        let formatter = dateFormatter(forFormat: Constants.apiFormat) as DateFormatter
        formatter.locale = Locale(identifier: "en_US_POSIX")

        return formatter
    }()

    public static var userEndDateShortFormatter: DateFormatter = {
        let formatter = dateFormatter(forFormat: Constants.shortDateFormat) as DateFormatter
        formatter.timeZone = TimeZone(abbreviation: "UTC")
        formatter.locale = Locale(identifier: "en_US_POSIX")

        return formatter
    }()

    public static var birthDateShortFormatter: DateFormatter = {
        let formatter = dateFormatter(forFormat: Constants.shortDateFormat)
        formatter.locale = Locale(identifier: "en_US_POSIX")

        return formatter
    }()

    public static var hourDateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.timeStyle = .short
        formatter.dateStyle = .none

        return formatter
    }()

    public static var futureLiveDiscussionFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(
                template: Constants.futureLiveDiscussionFormat
            )
        ) as DateFormatter
    }()

    public static var weekdayMonthDayFormatter: DateFormatter = {
        dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: Constants.weekdayMonthDayFormat)
        ) as DateFormatter
    }()

    public static var giftDateFormatter: DateFormatter = {
        let format =
            ["CA", "US", "CN", "JP", "KR"].contains(DateFormatter.regionCode)
            ? Constants.giftDateFormat1
            : Constants.giftDateFormat2

        return dateFormatter(
            forFormat: DateFormatter.localizedDateFormat(template: format)
        ) as DateFormatter
    }()

    public static let boxScoreLastGamesFormatter = dateFormatter(
        forFormat: DateFormatter.localizedDateFormat(template: Constants.boxScoreLastGamesFormat)
    )

    public static let liveRoomDateFormatter = dateFormatter(
        forFormat: DateFormatter.localizedDateFormat(template: Constants.liveRoomCreatedAtFormat)
    )

    public static let podcastEpisodeDateFormatter = dateFormatter(
        forFormat: DateFormatter.localizedDateFormat(template: Constants.podcastEpisodeDateFormat)
    )

    public static var feedYearAgoFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .none

        return formatter
    }()

    public static var iso8601ShortFormatter: DateFormatter = dateFormatter(
        forFormat: DateFormatter.localizedDateFormat(template: Constants.iso8601ShortFormat)
    )

    // MARK: - Components Formatters

    public static var shortComponentsFormatter: DateComponentsFormatter = {
        let formatter = DateComponentsFormatter()
        formatter.unitsStyle = .short
        formatter.allowedUnits = [.minute, .second]
        formatter.maximumUnitCount = 1

        return formatter
    }()

    public static var podcastSliderShortTimeFormatter: DateComponentsFormatter = {
        let formatter = DateComponentsFormatter()
        formatter.unitsStyle = .positional
        formatter.allowedUnits = [.minute, .second]
        formatter.zeroFormattingBehavior = [.pad]

        return formatter
    }()

    public static var podcastSliderTimeFormatter: DateComponentsFormatter = {
        let formatter = DateComponentsFormatter()
        formatter.unitsStyle = .positional
        formatter.allowedUnits = [.minute, .second, .hour]
        formatter.zeroFormattingBehavior = [.default]

        return formatter
    }()

    // MARK: - ISO8601DateFormatter

    public static var iso8601Formatter: ISO8601DateFormatter = {
        return ISO8601DateFormatter()
    }()

    // MARK: - Helpers

    public static func dateFormatter(forFormat format: String) -> DateFormatter {
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .iso8601)
        formatter.locale = Locale.current
        formatter.timeZone = NSTimeZone.default
        formatter.dateFormat = format

        return formatter
    }

    public static var fullComponentsFormatter: DateComponentsFormatter = {
        let formatter = makeDayHourMinuteFormatter()
        formatter.unitsStyle = .full
        formatter.zeroFormattingBehavior = .dropAll
        formatter.maximumUnitCount = 1

        return formatter
    }()

    public static var abbreviatedComponentsFormatter: DateComponentsFormatter = {
        let formatter = makeDayHourMinuteFormatter()
        formatter.unitsStyle = .abbreviated
        formatter.zeroFormattingBehavior = .dropAll
        formatter.maximumUnitCount = 1

        return formatter
    }()

    private static func makeDayHourMinuteFormatter() -> DateComponentsFormatter {
        let formatter = DateComponentsFormatter()

        formatter.allowedUnits = [
            NSCalendar.Unit.day,
            NSCalendar.Unit.hour,
            NSCalendar.Unit.minute,
        ]

        return formatter
    }
}
