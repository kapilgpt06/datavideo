package datavideo

class User {

    String userId
    String email
    String name
    String accessToken
    String refreshToken

    static constraints = {
        userId nullable: true
        accessToken nullable: true
        refreshToken nullable: true
    }
}
