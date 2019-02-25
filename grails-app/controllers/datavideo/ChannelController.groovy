package datavideo

class ChannelController {

    ChannelService channelService
    def list(){
        String userEmail=String.valueOf(session.getAttribute("userEmail"))
        List channelList=channelService.getChannelList(userEmail)
        [channelList:channelList]
    }

    def index(String userId) {
        String redirectUrl = "${createLink(controller:"channel", action: "callBack", absolute: true)}"
        String oauthUrl=channelService.generateOauthURL(redirectUrl)
        redirect(url:oauthUrl)
    }

    def callBack(){
        Map token=channelService.generateTokens(params.code as String)
println token
        Map channelDetails=channelService.getChannel(token.accessToken,token.refreshToken)
        String userEmail=String.valueOf(session.getAttribute("userEmail"))
        if(channelService.isChannelExist(channelDetails.channelId)){
            channelService.updateToken(channelDetails)
            flash.message="Channel Already Exist(Token are upadte)"
            redirect(action: "list")
        }else{
            channelService.saveChannel(channelDetails,userEmail)
            flash.message="Channel is Added Successfully"
            redirect(action: "list")
        }
     }

}
