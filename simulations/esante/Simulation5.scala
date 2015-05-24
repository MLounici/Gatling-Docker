package curapy

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import java.util.concurrent.ThreadLocalRandom

class Simulation5 extends Simulation {

  
  object Browse {
	  val browse = exec(http("page_accueil")
			  .get("/")
			  .check(status.is(200)))
			  .pause(1)  
			  
	
  }
  
  object Edit {

    val headers_1 = Map("Content-Type" -> """application/x-www-form-urlencoded""")

    val login = exec(http("Form")
        .get("/login")
		.check(regex("""<input type="hidden" name="_csrf_token" value="(.*)" />""").saveAs("_csrf_token")))
      .pause(1)
      .exec(http("Post")
        .post("/login_check")
        .headers(headers_1)
        .formParam("""name""", """login_form""")
        .formParam("""_username""", """therapeute1""")
        .formParam("""_password""", """therapeute1""")
		.formParam("_csrf_token", "${_csrf_token}")
        .check(status.is(200))
		.check(css("#userDialog")))
		
    val show_clinic1 = exec(http("page_clinique1")
	    .get("/user/clinique1")
		.check(status.is(200))
		.check(css("#listJeux")))
		
    val show_clinic6 = exec(http("page_clinique6")
	    .get("/user/clinique6")
		.check(status.is(200))
		.check(css("#listJeux")))
  }
  
  
  object Api {
  
		val headers_1 = Map("Content-Type" -> """application/x-www-form-urlencoded""")
		
		val login = exec(http("request_token")
			  .post("/oauth/v2/token")
			  .headers(headers_1)
			  .formParam("""client_id""", """11_2tsud9a3vq04wg4gsskwkc4gsk0kgkgocs4so4kw0o8g440okw""")
			  .formParam("""client_secret""", """5nzxj18jgio0cgc8c4c4kw488okscgsc0gkw0gwwc4kscowc4""")
			  .formParam("""grant_type""", """password""")
			  .formParam("""username""", """patient1""")
			  .formParam("""password""", """patient1""")
			  .check(jsonPath("$.access_token").saveAs("access_token")))
			  .pause(1)			  
			  
	  val request_1 = exec(http("request_user_data")
			  .post("/api/users/datas")
			  .headers(headers_1)
			  .header("Authorization", """Bearer ${access_token}""")
			  .check(regex("""<UserId>(.*)</UserId>""")))		  
			  .pause(1)  
  
  }
  


  val httpConf = http
    .baseURL("http://192.168.1.45")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0")

  val users = scenario("Users").exec(Browse.browse)
  val admins = scenario("Admins").exec(Browse.browse, Edit.login, Edit.show_clinic1, Edit.show_clinic6)
  val api_users = scenario("API Users").exec(Api.login, Api.request_1)
 

  setUp(
    users.inject(rampUsers(20) over (30 seconds)),
    admins.inject(rampUsers(5) over (30 seconds)),
	api_users.inject(rampUsers(10) over (30 seconds))
  ).protocols(httpConf)
}
