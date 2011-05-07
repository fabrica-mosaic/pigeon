package br.eng.mosaic.pigeon.server.socialnetwork;

public class FacebookResolver extends SocialNetworkResolver {
	
	private FacebookConfiguration fbConfig;
	
	protected String mount( String command ) {
		return pigeonConfig.fb_root + command;
	}
	
	@Deprecated @Override public String getApplicationInfo(String fbuid, String accessToken) {
		String query = 
			"SELECT display_name, description " +
				"FROM application WHERE app_id= :param1";
		return fetch(query, accessToken, -1, fbuid);
	}
	
	public String getAccessTokenFromApplication() {
		String command = RequestMethod.fb_oauth_access_token.method
			+ "?client_secret=" + fbConfig.secret
			+ "&client_id=" + fbConfig.id
			+ "&grant_type=" + "client_credentials";
	
		return mount( command );
	}
	
	public String getUrlStartConnection(String callbackUri) {
		String cURLCallback = pigeonConfig.app_root + callbackUri; 
		String command = RequestMethod.fb_oauth_authorize.method 
			+ "?client_id=" + fbConfig.id 
			+ "&redirect_uri=" + cURLCallback + "?" 
			+ "&scope=email,user_about_me";
		return mount( command );
	}
	
	public String getAccessTokenFromUser(String callbackUri, String hash) {
		String callbackUrl = pigeonConfig.app_root + callbackUri;
		String command = RequestMethod.fb_oauth_access_token.method
			+ "?client_id=" + fbConfig.id
			+ "&client_secret=" + fbConfig.secret
			+ "&code=" + hash
			+ "&redirect_uri=" + callbackUrl;
		
		return mount( command );
	}
	
	public String getBasicUserInfo(String callbackUri, String token) {
		String commandToken = ResponseAttribute.fb_access_token.key + token; 
		String command = RequestMethod.fb_user_info.method 
			+ "?" + commandToken
			+ "&client_id=" + fbConfig.id
			+ "&redirect_uri=" + pigeonConfig.app_root 
			+ callbackUri;
		return mount( command );
	}
	
	public String getPictureFromUser(String token) {
		String command = RequestMethod.fb_user_picture.method
			+ "?type=large"
			+"&" + ResponseAttribute.fb_access_token.key  
			+ token;
		return mount( command );
	}
	
	public void setFbConfig(FacebookConfiguration fbConfig) {
		this.fbConfig = fbConfig;
	}
	
}