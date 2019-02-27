package datavideo

class Channel {

    String channelId
    String channelName
    User owner
    String accesssToken
    String refreshToken
    Date tokenCreatedOn

    static constraints = {
    }
}
