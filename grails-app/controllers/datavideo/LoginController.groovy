package datavideo

class LoginController {

    LoginService loginService

    def user(){

    }
    def index() {
        String redirectUrl = "${createLink(controller:"login", action: "callBack", absolute: true)}"
        String oauthUrl=loginService.generateOauthURL(redirectUrl)
        redirect(url:oauthUrl)
    }

    def callBack(){
        Map tokens=loginService.generateTokens(params.code as String)
        Map userDetail=loginService.generateUserDetails(tokens)
        if(loginService.verifyUser(userDetail)){
            loginService.insertDetail(userDetail)
            session.setAttribute("userEmail",userDetail.email)
            redirect(controller: "channel", action: "list" )
        }else{
            render "fail"
        }

    }


}
