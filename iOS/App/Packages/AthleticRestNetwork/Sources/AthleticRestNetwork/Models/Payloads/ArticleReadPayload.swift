public struct ArticleReadPayload: Codable {
    let articleId: Int
    let isRead: Bool
    public init(articleId: Int, isRead: Bool) {
        self.articleId = articleId
        self.isRead = isRead
    }
}
