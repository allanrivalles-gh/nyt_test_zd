//
//  CoffeeService.swift
//
//
//  Created by Eric Yang on 14/1/20.
//

import Combine
import Foundation

public struct CoffeeService {
    static var cancellables: [AnyCancellable] = []
    /**
     Get article from the API.
     - Parameter payload: the instance of [CoffeeGetArticlePayload](x-source-tag://CoffeeGetArticlePayload).
     - Parameter network: the network instance initialized by APIManager.
     - Returns: The ATHNetworkPublisher with [CommentsResponse](x-source-tag://CommentsResponse)
     ### Usage Example: ###
     ````
     let payload = CoffeeGetArticlePayload(articleId: articleId)
     let dataTask = CoffeeService.getArticle(for: payload, on: network)
     let ATHNetworkPublisher = dataTask.dataFuture
     ATHNetworkPublisher.whenComplete { response in
        switch response {
        case .success(let article):
        case .failure(let error):
     }
    ````
    */
    public static func getArticle(for payload: CoffeeGetArticlePayload, on network: Network)
        -> ATHNetworkPublisher<Data>
    {
        Service.request(for: NetworkAPIEndpoint.getArticle(payload: payload), on: network)
    }

    /**
     Get topic from the API.
     - Parameter payload: the instance of [CoffeeGetTopicPayload](x-source-tag://CoffeeGetTopicPayload).
     - Parameter network: the network instance initialized by APIManager.
     - Returns: The ATHNetworkPublisher with [CommentsResponse](x-source-tag://CommentsResponse)
     ### Usage Example: ###
     ````
     let payload = CoffeeGetTopicPayload(articleId: topicId, useCached: useCached)
     CoffeeService.getArticle(for: payload, on: network).whenComplete { response in
        switch response {
        case .success(let article):
        case .failure(let error):
     }
    ````
    */
    public static func getTopic(for payload: CoffeeGetTopicPayload, on network: Network)
        -> ATHNetworkPublisher<ATHCommunityTopic>
    {
        let future: ATHNetworkPublisher<ATHCommunityTopicCodable> = Service.requestAndDecode(
            for: NetworkAPIEndpoint.getArticle(payload: payload),
            on: network
        )
        return Future { promise in
            future.sink { completion in
                switch completion {
                case .failure(let error):
                    promise(.failure(error))
                default:
                    break
                }
            } receiveValue: { response in
                let podcastsFeed = ATHCommunityTopic(withCodable: response)
                promise(.success(podcastsFeed))
            }
            .store(in: &CoffeeService.cancellables)
        }
        .eraseToAnyPublisher()
    }

    /**
     Get related articles from the API.
     - Parameter payload: the instance of [CoffeeGetRelatedArticlesPayload](x-source-tag://CoffeeGetRelatedArticlesPayload).
     - Parameter network: the network instance initialized by APIManager.
     - Returns: The ATHNetworkPublisher with RelatedArticlesResponse (x-source-tag://RelatedArticlesResponse)
     ### Usage Example: ###
     ````
     let payload = CoffeeGetRelatedArticlesPayload(itemId: itemId)
     CoffeeService.getRelatedArticles(for: payload, on: network).whenComplete { response in
        switch response {
        case .success(let article):
        case .failure(let error):
     }
    ````
    */
    public static func getRelatedArticles(
        for payload: CoffeeGetRelatedArticlesPayload,
        on network: Network
    ) -> ATHNetworkPublisher<RelatedArticlesResponse> {
        Service.requestAndDecode(
            for: NetworkAPIEndpoint.getRelatedArticles(payload: payload),
            on: network
        )
    }

    public static func updateArticleSavedState(
        for payload: ArticleSavedPayload,
        on network: Network
    ) -> ATHNetworkPublisher<Data> {
        Service.request(
            for: NetworkAPIEndpoint.updateArticleSavedState(payload: payload),
            on: network
        )
    }

    public static func logArticleRating(articleId: String, ratingId: Int, on network: Network)
        -> ATHNetworkPublisher<Data>
    {
        Service.request(
            for: NetworkAPIEndpoint.logArticleRating(articleId: articleId, ratingId: ratingId),
            on: network
        )
    }

    public static func searchForArticle(with text: String, on network: Network)
        -> ATHNetworkPublisher<
            Data
        >
    {
        Service.request(for: NetworkAPIEndpoint.searchArticle(searchText: text), on: network)
    }
}
