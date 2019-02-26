package datavideo

import grails.converters.JSON
import grails.core.GrailsApplication
import grails.transaction.Transactional
import org.grails.web.json.JSONObject

@Transactional
class LoginService {

    final String AUTH_URL = 'https://accounts.google.com/o/oauth2/auth'
    final String TOKEN_URL = 'https://accounts.google.com/o/oauth2/token'
    final String SCOPE_URLS = "email https://www.googleapis.com/auth/userinfo.profile"
    final String callbackUrl = "http://localhost:8080/login/callBack"

    GrailsApplication grailsApplication
    String generateOauthURL(String redirectUrl){
        String clientId=grailsApplication.config.google.oauth.clientId
        String oauthUrl="${AUTH_URL}?scope=${SCOPE_URLS}&redirect_uri=${redirectUrl}&response_type=code&client_id=${clientId}&access_type=offline&approval_prompt=force"
        oauthUrl
  }


    Map generateTokens(String code) {
        String queryString = generateQueryStringForAccessToken(code)
        HttpURLConnection connection = loadConnectionSettings(queryString.size())
        postTokensRequest(connection, queryString)
        extractTokens(connection)
    }

    String generateQueryStringForAccessToken(String code) {
        String clientId=grailsApplication.config.google.oauth.clientId
        String clientSecret=grailsApplication.config.google.oauth.clientSecret


        StringBuilder queryString = new StringBuilder("code=")
        queryString.with {
            append(code);
            append("&client_id=");
            append(encodeInUTF8(clientId));
            append("&client_secret=");
            append(encodeInUTF8(clientSecret));
            append("&redirect_uri=");
            append(encodeInUTF8(callbackUrl));
            append("&grant_type=");
            append(encodeInUTF8('authorization_code'))
        }
        return queryString.toString()
    }

    HttpURLConnection loadConnectionSettings(Long querySize) {
        URL url = new URL(TOKEN_URL)
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", "" + querySize);
        connection.setRequestProperty("Host", "accounts.google.com")
        return connection
    }

    Map extractTokens(HttpURLConnection connection) {
        String accessToken
        String refreshToken
        connection.connect()
        String resultData = connection.content.text // It will contain all the information related to access_token, and its expire time.
        def responseJson = JSON.parse(resultData)
        accessToken = responseJson?.access_token
        refreshToken = responseJson?.refresh_token
        Map tokens = [:]
        tokens.accessToken = accessToken
        tokens.refreshToken = refreshToken
        return tokens
    }
    def postTokensRequest(HttpURLConnection connection, String queryString) {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
        outputStreamWriter.write(queryString);
        outputStreamWriter.flush()


    }
    String encodeInUTF8(String val) {
        URLEncoder.encode(val,"UTF-8");
    }

    Map generateUserDetails(Map token){
        String AUTH_URL="https://www.googleapis.com/oauth2/v1/userinfo"

        URL url=new URL("${AUTH_URL}?access_token=${token.accessToken}")
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET")
        connection.connect()
        String resultData=connection.content.text
        JSONObject jsonData=(JSONObject)JSON.parse(resultData)

        Map userDetail=[:]
        userDetail.userId=jsonData.id
        userDetail.email=jsonData.email
        userDetail.name=jsonData.name
        userDetail.accessToken=token.accessToken
        userDetail.refreshToken=token.refreshToken

        userDetail
    }

    boolean verifyUser(Map userDetail){
        String email=userDetail.email
        User.findByEmail(email)
    }

    def insertDetail(Map userDetail){
        User user=User.findByEmail(userDetail.email)
        user.userId=userDetail.userId
        user.accessToken=userDetail.accessToken
        user.refreshToken=userDetail.refreshToken

        user.save(flush:true)
    }
}
