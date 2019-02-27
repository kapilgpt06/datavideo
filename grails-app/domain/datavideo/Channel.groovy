package datavideo

class Channel {

    String channelId
    String channelName
    User owner
    String accesssToken
    String refreshToken
    Date tokenCreatedOn
    Date dateCreated


    static constraints = {
    }
}
