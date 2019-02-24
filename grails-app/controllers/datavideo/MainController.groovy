package datavideo


import org.springframework.core.task.TaskExecutor

class MainController {
    ConvertToVideoService convertToVideoService
    MainService mainService
    def makeVideo() {
        TaskExecutor
        convertToVideoService.convert();
        render"Success"
    }

    def demo(){
        final String AUTH_URL = 'https://accounts.google.com/o/oauth2/auth'
//        final String SCOPE_URLS = "https://www.googleapis.com/auth/youtube.upload" // There can be many space delimited urls showing the permissions of the app.
//        final String SCOPE_URLS = "email https://www.googleapis.com/auth/userinfo.profile" // There can be many space delimited urls showing the permissions of the app.
        final String SCOPE_URLS = "https://www.googleapis.com/auth/youtube.upload" // There can be many space delimited urls showing the permissions of the app.
        String redirectUrl = "${createLink(controller:"main", action: "getTokens", absolute: true)}"
        String clientId="1023196152723-jklvq6upa176j9vitue0s7mkbedr1lbj.apps.googleusercontent.com"
        redirect(url: "${AUTH_URL}?scope=${SCOPE_URLS}&redirect_uri=${redirectUrl}&response_type=code&client_id=${clientId}&access_type=offline&approval_prompt=force")
    }

    def getTokens(){
        println "params=="+params
        String code=params.code
        println "code=="+code
        Map token=mainService.generateTokens(code)
        println token.accessToken
   /*     String AUTH_URL="https://www.googleapis.com/oauth2/v1/userinfo"
        URL url=new URL("${AUTH_URL}?access_token=${token.accessToken}")
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET")
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        println response
        br.close();
        render "done"*/
//        redirect(url: "${AUTH_URL}?access_token=${token.accessToken}")


        redirect(action: "token",params:[accessToken:token.accessToken])
//       redirect(action: "token",params:[accessToken:token.accessToken])
    }
    def token(){
        String accessToken=params.accessToken
        mainService.uploadVideo(accessToken)
        render accessToken
    }
    def userData(){}

    def getData(){

    }



}
