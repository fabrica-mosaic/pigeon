package br.eng.mosaic.pigeon.server.socialnetwork;

import static br.eng.mosaic.pigeon.server.socialnetwork.SocialNetworkResolver.ResponseAttribute.fb_access_token;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import br.eng.mosaic.pigeon.common.dto.UserSocialInfo;
import br.eng.mosaic.pigeon.infra.exception.NotImplemetedYetException;
import br.eng.mosaic.pigeon.server.helper.IOFetchContent;
import br.eng.mosaic.pigeon.server.socialnetwork.SocialNetworkResolver.ResponseAttribute;

public class FacebookClient {
	
	private IOFetchContent ioFetch;
	private Tagger tagger;
	private FacebookResolver resolver;
	
	public FacebookClient() {
		ioFetch = new IOFetchContent();
		tagger = new Tagger();
	}
	
	public String getAccessTokenFromApplication() {
		String cURL = resolver.getAccessTokenFromApplication();
		String response = ioFetch.getContent( cURL );
		return tagger.get(response, fb_access_token);
	}
	
	public String getStartConnection(String callback) {
		return resolver.getUrlStartConnection( callback );
	}
	
	/*
	 * TODO mudar para ao inves de > resolver.getAccessTokenFromUser(callback, hash);
	 * 		chamar resolver.configure( ACCESS_TOKEN_FROM_USER, callback, hash )
	 * 
	 * o tratamento no resolver seria pegar o array de strings e quem chamou 
	 * 		vai saber tratar e implementa-lo adequadamente
	 */
	public String getAccessTokenFromUser(String callbackUri, String hash) {
		String cURL = resolver.getAccessTokenFromUser(callbackUri, hash);
		String response = ioFetch.getContent( cURL );
		return tagger.getAccessToken( response );
	}
	
	// TODO refatorar para usar algum parser json e melhorar coesao
	public UserSocialInfo getBasicUserInfo(String callbackUri, String token) {
		String cURL = resolver.getBasicUserInfo( callbackUri, token );
		String response = ioFetch.getContent( cURL );
		JSONObject obj = tagger.getJSONObject( response );

		UserSocialInfo user;
		try {
			String name = obj.getString("name");
			String email = obj.getString("email");
			user = new UserSocialInfo(name, email, token);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		
		return user;
	}
	
	public byte[] getPicture(String token) throws ClientProtocolException, URISyntaxException, IOException {
		String cURL = resolver.getPictureFromUser( token );
		URI uri = getUriPicture( cURL );
		return ioFetch.getStream( uri );
	}
	
	private URI getUriPicture( String cURL ) throws URISyntaxException {
		String[] partialUri = cURL.split("\\?");
		URI uri = new URI("https", "graph.facebook.com", 
				"/me/picture", partialUri[1], null);
		return uri;
	}
	
	public void postMessage(String message) {
		throw new NotImplemetedYetException();
	}

	class Tagger {
		
		private boolean isInvalid( String response, ResponseAttribute alias ) {
			if ( response == null || response.isEmpty() )
				return true; // conteudo response nao veio do server ou eh vazio

			if ( !response.contains( alias.key ) )
				return true; // propriedade inexistente. verifique o alias ou a string cURL
			
			return false;
		}
		
		private String get(String response, ResponseAttribute alias) {
			String unfortunately = "invalid_content";
			if ( isInvalid(response, alias) )
				return unfortunately;
			
			String[] puzzle = response.split( alias.key );
			if ( puzzle.length != 2  )
				return unfortunately;
			
			return puzzle[1];
		}
		
		private String getAccessToken(String response) {
			
			Map<String, String> entries = new HashMap<String, String>(); 
			String[] params = response.split("&");
			String equal = "=";
			for (String entry : params) {
				if ( entry == null || entry.isEmpty() )
					continue; 
				
				String[] pair = entry.split( equal );
				entries.put(pair[0] + equal, pair[1]);
			}
			
			return entries.get( fb_access_token.key );
		}
		
		private JSONObject getJSONObject( String content ) {
			JSONObject jsonObj = null;
			try {
				jsonObj = new JSONObject(content);
			} catch (JSONException e) {
				throw new RuntimeException("erro ao extrair objeto json");
			} 
			return jsonObj;
		}
		
	}
	
	public void setResolver(FacebookResolver resolver) {
		this.resolver = resolver;
	}

}