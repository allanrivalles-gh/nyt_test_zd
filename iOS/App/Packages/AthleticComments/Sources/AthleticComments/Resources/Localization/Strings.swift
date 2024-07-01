//
//  Strings.swift
//
//
//  Created by Kyle Browning on 7/13/22.
//

import AthleticFoundation
import Foundation

internal enum Strings: String, Localizable, CaseIterable {
    var bundle: Bundle { .module }
    var baseFilename: String { "en" }

    case addingYourComment
    case athleticStaff
    case areYouSure
    case beTheFirstToCommentOnThisStory
    case cancel
    case comment
    case comments
    case commentingOn
    case commentsTitle
    case commentFlagReason1
    case commentFlagReason2
    case commentFlagReason3
    case commentsReviewInfo
    case commentingPlayLabelTitle
    case commentToast
    case delete
    case deleteComment
    case discussionTitle
    case disabledComments
    case edit
    case editingYourComment
    case flag
    case flagComment
    case flaggingComment
    case flaggedComment
    case fromTwitter
    case genericError
    case justNow
    case loading
    case likedCommentError
    case mostLiked
    case newest
    case noComments
    case oldest
    case qandaTitle
    case replyingTo
    case share
    case staff
    case teamThreadsFollowers
    case temporaryBanMessagePrefix
    case temporaryBanCodeOfConduct
    case temporaryBanCtaFormatDays
    case temporaryBanCtaFormatTomorrow
    case trending
    case undo
    case unlikedCommentError
    case visibleFlairsMessage
    case writeAComment
    case yesterday
    case change
    case close
}
