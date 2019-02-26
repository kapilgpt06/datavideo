package datavideo

class LogoutController {

    def index() {
        session.invalidate()
        flash.messsage="successfullt logout"
        redirect(controller:'login', action:'user')
    }
}
