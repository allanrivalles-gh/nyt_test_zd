public struct UpdateUserPayload: Encodable {
    public let userId: Int
    public let fname: String
    public let lname: String
    public let editEmail: String

    private enum CodingKeys: String, CodingKey {
        case userId = "userid"
        case fname = "fname"
        case lname = "lname"
        case editEmail = "edit_email"
    }

    public init(userId: Int, fName: String, lName: String, email: String) {
        self.userId = userId
        fname = fName
        lname = lName
        editEmail = email
    }
}
