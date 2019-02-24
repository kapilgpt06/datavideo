package datavideo

import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class IntegerateChannelService {

    final String AUTH_URL = 'https://accounts.google.com/o/oauth2/auth'
    final String TOKEN_URL = 'https://accounts.google.com/o/oauth2/token'
    final String SCOPE_URLS = "https://www.googleapis.com/auth/youtube"
    final String clientId="1023196152723-jklvq6upa176j9vitue0s7mkbedr1lbj.apps.googleusercontent.com"
    final String callbackUrl = "http://localhost:8080/integerateChannel/callBack"
    final String clientSecret = "RYSGFOBt6VsHUYFVPxQlBSib"

    def serviceMethod() {

    }
    String generateOauthURL(String redirectUrl){

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
        println connection.getResponseMessage()
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

}
