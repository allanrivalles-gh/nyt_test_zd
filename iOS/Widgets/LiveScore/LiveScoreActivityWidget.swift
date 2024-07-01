//
//  LiveScoreActivityWidget.swift
//  HeadlinesWidgetExtension
//
//  Created by Duncan Lau on 9/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import ActivityKit
import AthleticUI
import SwiftUI
import WidgetKit

@available(iOS 16.2, *)
struct LiveScoreActivityWidget: Widget {

    init() {
        AthleticUI.registerFonts()
    }

    let rootDirectory = FileManager.default
        .containerURL(forSecurityApplicationGroupIdentifier: "group.theathletic.main")!
        .appending(component: "liveActivities", directoryHint: .isDirectory)

    var body: some WidgetConfiguration {
        ActivityConfiguration(for: LiveScoreAttributes.self) { context in
            LockScreenLiveActivityView(
                context: context,
                firstTeamLogo: context.attributes.firstTeamInfo.teamLogo,
                secondTeamLogo: context.attributes.secondTeamInfo.teamLogo
            )
        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    HStack(spacing: 20) {
                        Image("logo")
                            .resizable()
                            .frame(width: 20, height: 20)

                        Text("\(context.state.homeTeamScore)")
                            .fontStyle(.calibreHeadline.m.semibold)
                    }
                    .padding(.top, 10)
                    .padding(.leading, 20)
                }

                DynamicIslandExpandedRegion(.trailing) {
                    HStack(spacing: 20) {
                        Text("\(context.state.awayTeamScore)")
                            .fontStyle(.calibreHeadline.m.semibold)

                        Image("logo")
                            .resizable()
                            .frame(width: 20, height: 20)
                    }
                    .padding(.top, 10)
                    .padding(.trailing, 20)
                }

                DynamicIslandExpandedRegion(.center) {
                    VStack(spacing: 2) {
                        Text("FT")
                            .lineLimit(1)
                            .fontStyle(.calibreHeadline.s.medium)
                        Text("Mon, 6 Feb")
                            .lineLimit(1)
                            .fontStyle(.calibreUtility.s.medium)
                    }
                }

                DynamicIslandExpandedRegion(.bottom) {
                    VStack {
                        HStack(spacing: 20) {
                            Spacer()
                            VStack {
                                Text("H.Kane 40' 62' 90'")
                                    .lineLimit(1)
                                    .fontStyle(.calibreUtility.s.medium)
                                    .foregroundColor(.chalk.dark600)
                            }

                            Image(systemName: "soccerball")
                                .resizable()
                                .frame(width: 10, height: 10)

                            VStack {
                                Text("M.Rashford 43' 67'")
                                    .lineLimit(1)
                                    .fontStyle(.calibreUtility.s.medium)
                                    .foregroundColor(.chalk.dark600)
                            }
                            Spacer()
                        }

                    }
                    .padding(.vertical, 10)
                }
            } compactLeading: {
                HStack {
                    Image("logo")
                        .resizable()
                        .frame(width: 7, height: 7)
                    Text("\(context.attributes.firstTeamInfo.teamName)")
                        .fontStyle(.calibreUtility.xs.regular)
                    Text("\(context.state.homeTeamScore)")
                        .fontStyle(.calibreUtility.xs.regular)
                }
                .padding(.leading, 5)
            } compactTrailing: {
                HStack {
                    Text("\(context.state.awayTeamScore)")
                        .fontStyle(.calibreUtility.xs.regular)
                    Text("\(context.attributes.secondTeamInfo.teamName)")
                        .fontStyle(.calibreUtility.xs.regular)
                    Image("logo")
                        .resizable()
                        .frame(width: 7, height: 7)
                }
            } minimal: {
                Image("logo")
                    .resizable()
                    .frame(width: 16, height: 16)
                    .foregroundColor(.chalk.dark800)
            }
        }
    }
}

@available(iOS 16.2, *)
struct LockScreenLiveActivityView: View {
    let context: ActivityViewContext<LiveScoreAttributes>
    let firstTeamLogo: UIImage?
    let secondTeamLogo: UIImage?

    var body: some View {
        VStack(spacing: 0) {
            VStack(spacing: 0) {
                HStack {

                    if let firstTeamLogo {
                        Image(uiImage: firstTeamLogo)
                            .resizable()
                            .frame(width: 50, height: 50)
                    } else {
                        Image(systemName: "soccerball")
                            .resizable()
                            .frame(width: 50, height: 50)
                    }

                    Text("\(context.state.homeTeamScore)")
                        .fontStyle(.calibreHeadline.m.semibold)
                    Spacer()
                    Text("FT")
                        .fontStyle(.calibreHeadline.m.semibold)
                    Spacer()
                    Text("\(context.state.awayTeamScore)")
                        .fontStyle(.calibreHeadline.m.semibold)

                    if let secondTeamLogo {
                        Image(uiImage: secondTeamLogo)
                            .resizable()
                            .frame(width: 50, height: 50)
                    } else {
                        Image(systemName: "soccerball")
                            .resizable()
                            .frame(width: 50, height: 50)
                    }
                }
                .padding(.horizontal, 50)
                .padding(.vertical, 10)
            }
            .foregroundColor(Color.chalk.dark700)
            .background(Color.chalk.dark300)

            VStack {
                Divider()
                HStack(spacing: 20) {
                    Spacer()
                    VStack {
                        Text("H.Kane 40' 62' 90'")
                            .lineLimit(1)
                            .fontStyle(.calibreUtility.s.medium)
                        Text("Son 40' 62' 90'")
                            .lineLimit(1)
                            .fontStyle(.calibreUtility.s.medium)
                    }

                    Image(systemName: "soccerball")
                        .resizable()
                        .frame(width: 10, height: 10)

                    VStack {
                        Text("M.Rashford 43' 67'")
                            .lineLimit(1)
                            .fontStyle(.calibreUtility.s.medium)
                    }
                    Spacer()
                }
                .padding(.bottom, 10)

            }
            .foregroundColor(Color.chalk.dark300)
            .background(Color.chalk.dark700)

        }
    }
}

@available(iOS 16.2, *)
@available(iOSApplicationExtension 16.2, *)
struct LiveScoreActivityWidget_Previews: PreviewProvider {
    static let activityAttributes = LiveScoreAttributes(
        firstTeamInfo: LiveScoreAttributes.TeamInfo(
            teamName: "MUN",
            logoFileName: "1234_first"
        ),
        secondTeamInfo: LiveScoreAttributes.TeamInfo(
            teamName: "TOT",
            logoFileName: "1234_second"
        ),
        gameId: "0"
    )
    static let activityState = LiveScoreAttributes.ContentState(homeTeamScore: 3, awayTeamScore: 2)

    static var previews: some View {
        Group {
            activityAttributes
                .previewContext(activityState, viewKind: .content)
                .previewDisplayName("Notification")

            activityAttributes
                .previewContext(activityState, viewKind: .dynamicIsland(.compact))
                .previewDisplayName("Compact")

            activityAttributes
                .previewContext(activityState, viewKind: .dynamicIsland(.expanded))
                .previewDisplayName("Expanded")

            activityAttributes
                .previewContext(activityState, viewKind: .dynamicIsland(.minimal))
                .previewDisplayName("Minimal")
        }
    }
}
