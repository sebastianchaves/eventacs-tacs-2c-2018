package com.eventacs.external.eventbrite.model;
import com.eventacs.external.telegram.client.JdbcDao.UserData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetAccessToken implements java.io.Serializable{

    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String scope;
    private String username;
    //private List<Authority> role;
    private UserData principal;

    public GetAccessToken() {
    }

    public GetAccessToken(String access_token, String token_type, String refresh_token, String expires_in, String scope, String username, UserData principal) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.refresh_token = refresh_token;
        this.expires_in = expires_in;
        this.scope = scope;
        this.username = username;
        this.principal = principal;
    }


    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUsername() {
        //return username;
        return principal.getUsername();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserData getPrincipal() {
        return principal;
    }

    public void setPrincipal(UserData principal) {
        this.principal = principal;
    }
}