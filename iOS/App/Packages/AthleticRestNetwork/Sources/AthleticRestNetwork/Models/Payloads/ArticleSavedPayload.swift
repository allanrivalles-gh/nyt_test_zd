public struct ArticleSavedPayload: Encodable {
    let articleId: Int
    let isSaved: Bool

    public init(articleId: Int, isSaved: Bool) {
        self.articleId = articleId
        self.isSaved = isSaved
    }

    // Excluding the 'isSaved' key when encode
    private enum CodingKeys: String, CodingKey {
        case articleId
    }
}
