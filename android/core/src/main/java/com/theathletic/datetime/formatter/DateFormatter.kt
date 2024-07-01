package com.theathletic.datetime.formatter

import com.theathletic.datetime.Datetime
import com.theathletic.formatter.Formatter
import com.theathletic.formatter.FormatterWithParams

interface DateFormatter : Formatter<Datetime>

interface DateFormatterWithParams<Params> : FormatterWithParams<Datetime, Params>