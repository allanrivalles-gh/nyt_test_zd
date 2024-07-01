//
//  CommentsService.swift
//
//
//  Created by Eric Yang on 18/12/19.
//

import Foundation

public struct CommentsService {
    /**
     Fetch comments of an article from the API.
     - Parameter payload: the instance of [CommentsGetPayload](x-source-tag://CommentsResponse).
     - Parameter network: the network instance initialized by APIManager.
     - Returns: The ATHNetworkPublisher with [CommentsResponse](x-source-tag://CommentsResponse)
     ### Usage Example: ###
     ````
     let payload = CommentsGetPayload(withArticleId: articleId, useCached: false)
     CommentsService.getComments(for: payload, on: network).whenComplete { response in
        switch response {
        case .success(let result):
        case .failure(let error):
    }
    ````
    */
    public static func getComments(for payload: CommentsGetPayload, on network: Network)
        -> ATHNetworkPublisher<CommentsResponse>
    {
        Service.requestAndDecode(
            for: NetworkAPIEndpoint.getArticleComments(payload: payload),
            on: network
        )
    }

    /**
     Like or unlike a comment by updating the API.
     - Parameter payload: the instance of [CommentsToggleLikePayload](x-source-tag://CommentsToggleLikePayload).
     - Parameter network: the network instance initialized by APIManager.
     - Returns: The ATHNetworkPublisher with [SimpleResult](x-source-tag://SimpleResult)
     ### Usage Example: ###
     ````
     let payload = CommentsToggleLikePayload(withId: commentId, isLiked: true)
     CommentsService.toggleLike(for: payload, on: network).whenComplete { response in
        switch response {
        case .success:
        case .failure(let error):
    }
    ````
    */
    public static func toggleLike(for payload: CommentsToggleLikePayload, on network: Network)
        -> ATHNetworkPublisher<SimpleResult>
    {
        Service.requestAndDecode(for: NetworkAPIEndpoint.toggleLike(payload: payload), on: network)
    }

    /**
     Post the comment to API.
     - Parameter payload: the payload of post comment.
     - Parameter network: the network instance initialized by APIManager.
     - Returns: The ATHNetworkPublisher with [CommentsResponse](x-source-tag://CommentsResponse)
     ### Usage Example: ###
     ````
     CommentsService.postComment(for: payload, on: network).whenComplete { response in
        switch response {
        case .success:
        case .failure(let error):
    }
    ````
    */
    public static func postComment(for payload: CommentsPostPayload, on network: Network)
        -> ATHNetworkPublisher<CommentsResponse>
    {
        Service.requestAndDecode(for: NetworkAPIEndpoint.postComment(payload: payload), on: network)
    }

    /**
     Flag the comment.
     - Parameter payload: the instance of [FlagCommentPayload](x-source-tag://FlagCommentPayload).
     - Parameter network: the network instance initialized by APIManager.
     - Returns: The ATHNetworkPublisher with [SimpleResult](x-source-tag://SimpleResult)
     ### Usage Example: ###
     ````
     CommentsService.flagComment(for: payload, on: network).whenComplete { response in
        switch response {
        case .success:
        case .failure(let error):
    }
    ````
    */
    public static func flagComment(for payload: FlagCommentPayload, on network: Network)
        -> ATHNetworkPublisher<SimpleResult>
    {
        Service.requestAndDecode(for: NetworkAPIEndpoint.flagComment(payload: payload), on: network)
    }

    /**
     Edit the comment.
     - Parameter payload: the instance of [EditCommentPayload](x-source-tag://EditCommentPayload).
     - Parameter network: the network instance initialized by APIManager.
     - Returns: The ATHNetworkPublisher with [SimpleResult](x-source-tag://SimpleResult)
     ### Usage Example: ###
     ````
     CommentsService.editComment(for: payload, on: network).whenComplete { response in
        switch response {
        case .success:
        case .failure(let error):
    }
    ````
    */
    public static func editComment(for payload: EditCommentPayload, on network: Network)
        -> ATHNetworkPublisher<SimpleResult>
    {
        Service.requestAndDecode(for: NetworkAPIEndpoint.editComment(payload: payload), on: network)
    }

    /**
     Delete the comment.
     - Parameter payload: the instance of [DeleteCommentPayload](x-source-tag://DeleteCommentPayload).
     - Returns: The ATHNetworkPublisher with [SimpleResult](x-source-tag://SimpleResult)
     ### Usage Example: ###
     ````
     CommentsService.deleteComment(for: payload, on: network).whenComplete { response in
        switch response {
        case .success:
        case .failure(let error):
    }
    ````
    */
    public static func deleteComment(for payload: DeleteCommentPayload, on network: Network)
        -> ATHNetworkPublisher<SimpleResult>
    {
        Service.requestAndDecode(
            for: NetworkAPIEndpoint.deleteComment(payload: payload),
            on: network
        )
    }

    /**
     Get last comment date.
     - Parameter payload: the instance of [GetLastCommentDatePayload](x-source-tag://GetLastCommentDatePayload).
     - Returns: The ATHNetworkPublisher with [LastestCommentData](x-source-tag://LastestCommentData)
     ### Usage Example: ###
     ````
     CommentsService.getLastCommentDate(for: payload, on: network).whenComplete { response in
        switch response {
        case .success(let lastestCommentData):
        case .failure(let error):
    }
    ````
    */
    public static func getLastCommentDate(
        for payload: GetLastCommentDatePayload,
        on network: Network
    ) -> ATHNetworkPublisher<LatestCommentData> {
        Service.requestAndDecode(
            for: NetworkAPIEndpoint.getLastCommentDate(payload: payload),
            on: network
        )
    }
}
