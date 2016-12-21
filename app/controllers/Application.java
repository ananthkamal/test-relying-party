package controllers;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Controller;

public class Application extends Controller {
	
	public static final String SCOPE = "openid profile email";
	public static final String GRANT_TYPE = "authorization_code";

	public static final String CLIENT_ID = Play.configuration.getProperty("client.id");
	public static final String CLIENT_SECRET = Play.configuration.getProperty("client.secret");
	public static final String REDIRECT_URI = Play.configuration.getProperty("client.redirect.uri");
	public static final String AUTHORIZE_ENDPOINT = Play.configuration.getProperty("authorize.endpoint");
	public static final String TOKEN_ENDPOINT = Play.configuration.getProperty("token.endpoint");
	public static final String USERINFO_ENDPOINT = Play.configuration.getProperty("subscriber.profile.endpoint");
	public static final String LOGIN_URL = Play.configuration.getProperty("client.login.url");
	public static final String AUTH_SERVER_LOGIN_URL = Play.configuration.getProperty("auth.server.login.url");
	public static final String LOGOUT_URL = Play.configuration.getProperty("auth.server.logout.url");

	public static void index() {
		render();
	}

	public static void login() {
		render();
	}

	public static void user(String code, String state, String error, String error_description) {
		Map<String, String> userPageData = new HashMap<String, String>();
		userPageData.put("code", code);
		userPageData.put("state", state);
		userPageData.put("error", error);
		userPageData.put("error_description", error_description);
		userPageData.put("logoutUrl", LOGOUT_URL);
	    userPageData.put("loginUrl", LOGIN_URL);
		render(userPageData);
	}

	public static JsonObject jwt(String code) {
		Integer nonceInt = new Random().nextInt(100000) + 1;
		String tokenEndPointUrl;
		try {
			tokenEndPointUrl = Application.TOKEN_ENDPOINT + "client_id=" + Application.CLIENT_ID + "&client_secret="
					+ Application.CLIENT_SECRET + "&code=" + code + "&scope="
					+ URLEncoder.encode(Application.SCOPE, "UTF-8") + "&redirect_uri="
					+ URLEncoder.encode(Application.REDIRECT_URI, "UTF-8") + "&grant_type=" + Application.GRANT_TYPE
					+ "&nonce=" + nonceInt.toString();
			System.out.println(tokenEndPointUrl);
			HttpResponse jwt = WS.url(tokenEndPointUrl).post();
			return jwt.getJson().getAsJsonObject();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JsonObject userInfo(String token){
        String userInfoEndpointUrl = Application.USERINFO_ENDPOINT + token;
        try {
	        // HttpResponse userInfoResponse = WS.url(userInfoEndpointUrl).get();
	        // return userInfoResponse.getJson().getAsJsonObject();
        	
	        return new JsonParser().parse("{\"firstName\":\"Demo\",\"lastName\":\"User\",\"email\":\"test@email.com\"}").getAsJsonObject();
        } catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void error(String error, String error_description) {
	    Map<String, String> errors = new HashMap<String, String>();
	    errors.put("error", error);
	    errors.put("description", error_description);

	    renderJSON(errors);
	}
}