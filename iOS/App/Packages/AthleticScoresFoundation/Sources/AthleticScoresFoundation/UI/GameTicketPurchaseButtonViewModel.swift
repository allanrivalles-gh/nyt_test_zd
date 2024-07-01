//
//  GameTicketPurchaseButtonViewModel.swift
//
//
//  Created by Mark Corbyn on 29/6/2023.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticUI
import Foundation

public struct GameTicketPurchaseButtonViewModel: Analytical {
    public struct ClickAnalyticsConfiguration {
        public let clickAnalyticsView: AnalyticsEvent.View
        public let clickAnalyticsObjectType: AnalyticsEvent.ObjectType
        public let clickAnalyticsObjectIdentifier: String
        public let clickAnalyticsSlateIdentifier: String?

        public init(
            clickAnalyticsView: AnalyticsEvent.View,
            clickAnalyticsObjectType: AnalyticsEvent.ObjectType,
            clickAnalyticsObjectIdentifier: String,
            clickAnalyticsSlateIdentifier: String?
        ) {
            self.clickAnalyticsView = clickAnalyticsView
            self.clickAnalyticsObjectType = clickAnalyticsObjectType
            self.clickAnalyticsObjectIdentifier = clickAnalyticsObjectIdentifier
            self.clickAnalyticsSlateIdentifier = clickAnalyticsSlateIdentifier
        }
    }

    public let title: String
    public let url: URL
    public let provider: String
    public let lightModeLogos: [ATHImageResource]
    public let darkModeLogos: [ATHImageResource]
    public let clickAnalyticsConfiguration: ClickAnalyticsConfiguration?
    public let analyticsDefaults: AnalyticsRequiredValues

    public var analyticData: AnalyticData {
        guard let clickAnalyticsConfiguration else {
            return AnalyticData()
        }

        return AnalyticData(
            click: AnalyticsEventRecord(
                verb: .click,
                view: clickAnalyticsConfiguration.clickAnalyticsView,
                element: .tickets,
                objectType: clickAnalyticsConfiguration.clickAnalyticsObjectType,
                objectIdentifier: clickAnalyticsConfiguration.clickAnalyticsObjectIdentifier,
                metaBlob: AnalyticsEvent.MetaBlob(
                    slate: clickAnalyticsConfiguration.clickAnalyticsSlateIdentifier,
                    ticketPartner: provider,
                    requiredValues: analyticsDefaults
                ),
                requiredValues: analyticsDefaults
            )
        )
    }

    public init(
        title: String,
        url: URL,
        provider: String,
        lightModeLogos: [ATHImageResource],
        darkModeLogos: [ATHImageResource],
        clickAnalyticsConfiguration: ClickAnalyticsConfiguration? = nil,
        analyticsDefaults: AnalyticsRequiredValues
    ) {
        self.title = title
        self.url = url
        self.provider = provider
        self.lightModeLogos = lightModeLogos
        self.darkModeLogos = darkModeLogos
        self.clickAnalyticsConfiguration = clickAnalyticsConfiguration
        self.analyticsDefaults = analyticsDefaults
    }

    public init?(
        tickets: GQL.GameTickets,
        locale: Locale = .current,
        clickAnalyticsConfiguration: ClickAnalyticsConfiguration? = nil,
        analyticsDefaults: AnalyticsRequiredValues
    ) {
        guard let url = URL(string: tickets.uri) else {
            return nil
        }

        let title = Self.makeTitle(
            prices: tickets.minPrice.map { $0.fragments.gameTicketsPrice },
            locale: locale
        )

        self.init(
            title: title,
            url: url,
            provider: tickets.provider,
            lightModeLogos: tickets.logosLightMode.map { $0.fragments.ticketLogo }.resources,
            darkModeLogos: tickets.logosDarkMode.map { $0.fragments.ticketLogo }.resources,
            clickAnalyticsConfiguration: clickAnalyticsConfiguration,
            analyticsDefaults: analyticsDefaults
        )
    }

    public init(
        widget: GQL.ScoresFeedGameTicketsWidgetBlock,
        ticketUrl: URL,
        clickAnalyticsConfiguration: ClickAnalyticsConfiguration? = nil,
        analyticsDefaults: AnalyticsRequiredValues
    ) {
        self.init(
            title: widget.text,
            url: ticketUrl,
            provider: widget.provider,
            lightModeLogos: widget.logosLightMode.map { $0.fragments.ticketLogo }.resources,
            darkModeLogos: widget.logosDarkMode.map { $0.fragments.ticketLogo }.resources,
            clickAnalyticsConfiguration: clickAnalyticsConfiguration,
            analyticsDefaults: analyticsDefaults
        )
    }

    private static func makeTitle(
        prices: [GQL.GameTicketsPrice],
        locale: Locale
    ) -> String {
        guard let localeBestPrice = prices.localeBestMatch(locale: locale) else {
            return Strings.buyTickets.localized
        }

        let price = localeBestPrice.localized(locale: locale)
        return String(
            format: Strings.ticketsFromPriceFormat.localized,
            price
        )
    }
}
