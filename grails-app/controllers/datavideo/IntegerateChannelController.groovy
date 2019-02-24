package datavideo

class IntegerateChannelController {

    IntegerateChannelService integerateChannelService

    def index() {
        String redirectUrl = "${createLink(controller:"integerateChannel", action: "callBack", absolute: true)}"
        String oauthUrl=integerateChannelService.generateOauthURL(redirectUrl)
        redirect(url:oauthUrl)
    }

    def callBack(){
        Map token=integerateChannelService.generateTokens(params.code as String)
        println token
        return "done"
    }
}
