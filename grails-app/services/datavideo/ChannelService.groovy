package datavideo

import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.ChannelListResponse
import com.google.common.collect.Lists
import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class ChannelService {

    final String AUTH_URL = 'https://accounts.google.com/o/oauth2/auth'
    final String TOKEN_URL = 'https://accounts.google.com/o/oauth2/token'
    final String clientId = "1023196152723-6shlrh538pnm6n8b4mggd2hnvr37eaf3.apps.googleusercontent.com"
    final String callbackUrl = "http://localhost:8080/channel/callBack"
    final String clientSecret = "_0-p-ZMCe561LJ6Am9jZxAbU"



    String generateOauthURL(String redirectUrl) {
        String SCOPE_URLS="https://www.googleapis.com/auth/youtube.upload https://www.googleapis.com/auth/youtube.readonly"
        String oauthUrl = "${AUTH_URL}?scope=${SCOPE_URLS}&redirect_uri=${redirectUrl}&response_type=code&client_id=${clientId}&access_type=offline&approval_prompt=force"
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
        String resultData = connection.content.text
        // It will contain all the information related to access_token, and its expire time.
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
        URLEncoder.encode(val, "UTF-8");
    }

    Map getChannel(String accessToken, String refreshToken) {
        Map channelDetail=[:]
        YouTube youtube
        try {

            HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            JsonFactory JSON_FACTORY = new JacksonFactory();

            Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                    "youtube-channel-list").build();
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet,contentDetails,statistics");
            parameters.put("mine", "true");

            YouTube.Channels.List channelsListMineRequest = youtube.channels().list(parameters.get("part").toString());
            if (parameters.containsKey("mine") && parameters.get("mine") != "") {
                boolean mine = (parameters.get("mine") == "true") ? true : false;
                channelsListMineRequest.setMine(mine);
            }

            ChannelListResponse response = channelsListMineRequest.execute();
            Map mapResponse=response


            channelDetail.title=String.valueOf(mapResponse.items[0].snippet.localized.title)
            channelDetail.channelId=String.valueOf(mapResponse.items[0].id)
            channelDetail.accessToken=accessToken
            channelDetail.refreshToken=refreshToken
        } catch (Exception e) {
            println e
        }
        channelDetail
    }
    boolean isChannelExist(String channelId){
        Channel.findByChannelId(channelId)
    }
    def saveChannel(Map channelDetails,String userEmail){
        println(channelDetails)
        User user=User.findByEmail(userEmail)
        Channel channel=new Channel(channelId: channelDetails.channelId,channelName: channelDetails.title,owner:user,
                accesssToken: channelDetails.accessToken,refreshToken: channelDetails.refreshToken )
        channel.save(flush:true)
    }
    def updateToken(Map channelDetail){
        Channel channel=Channel.findByChannelId(channelDetail.channelId)
        channel.accesssToken=channelDetail.accessToken
        channel.refreshToken=channelDetail.refreshToken
        channel.save(flush:true)
    }
    List<ChannelDTO> getChannelList(String userEmail){
        User currentUser=User.findByEmail(userEmail)
        List channelList=Channel.findAllByOwner(currentUser)
        List<ChannelDTO> channelListDTO=[]
        channelList.each {
            ChannelDTO channelDTO=new ChannelDTO()
            channelDTO.channelId=it.channelId
            channelDTO.channelName=it.channelName
            channelDTO.ownerEmail=it.owner.email
            channelListDTO<<channelDTO
        }
        channelListDTO
    }

}