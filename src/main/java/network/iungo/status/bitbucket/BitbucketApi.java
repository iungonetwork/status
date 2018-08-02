/*
 * BitbucketApi.java
 *
 * Copyright (c) 2018 IUNGO (https://iungo.network)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package network.iungo.status.bitbucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import network.iungo.status.bitbucket.auth.OauthCredentials;
import network.iungo.status.bitbucket.response.auth.AccessTokenResponse;
import network.iungo.status.bitbucket.response.commits.Commits;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Bitbucket REST API OAuth 2 client
 */
public class BitbucketApi {

    private static Logger LOG = LoggerFactory.getLogger(BitbucketApi.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private final static String TOKEN_LOCATION = "https://bitbucket.org/site/oauth2/access_token";
    private final static int READ_TIMEOUT = 10000;
    private final static int CONNECT_TIMEOUT = 10000;

    private String account;
    private byte[] consumerAuth;

    private String refreshToken;
    private String accessToken;
    private Long expiresSeconds;

    private void checkAuth() throws Exception {
        if (accessToken == null) {
            LOG.info("Access Token does not exist, authenticating");
            authorize();
        } else {
            long nowSeconds = System.currentTimeMillis() / 1000;
            if (nowSeconds >= expiresSeconds) {
                LOG.info("Access Token expired, refreshing");
                refresh();
            }
        }
    }

    private BitbucketApi() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Instantiates a new Bitbucket api.
     *
     * @param credentials the credentials
     */
    public BitbucketApi(OauthCredentials credentials) {
        this();
        this.account = credentials.getAccount();
        byte[] unencodedConsumerAuth = (credentials.getKey() + ":" + credentials.getSecret()).getBytes(StandardCharsets.UTF_8);
        consumerAuth = Base64.getEncoder().encode(unencodedConsumerAuth);
    }

    private String httpGet(String url) throws Exception {
        checkAuth();

        URL u = new URL(url);

        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);
        con.setConnectTimeout(CONNECT_TIMEOUT);
        con.setReadTimeout(READ_TIMEOUT);
        con.setInstanceFollowRedirects(false);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return content.toString();
    }

    /**
     * Get any resource from the repository
     *
     * @param <T>    the type parameter
     * @param url    the url
     * @param tClass the t class
     * @return the t
     * @throws Exception the exception
     */
    public <T> T get(String url, Class<T> tClass) throws Exception {
        String response = httpGet(String.format("https://api.bitbucket.org/2.0/repositories/%s/" + url, account));
        return objectMapper.readValue(response, tClass);
    }

    /**
     * Gets commits.
     *
     * @param repo the repo
     * @return the commits
     * @throws Exception the exception
     */
    public Commits getCommits(String repo) throws Exception {
        String response = httpGet(String.format("https://api.bitbucket.org/2.0/repositories/%s/%s/commits", account, repo));
        return objectMapper.readValue(response, Commits.class);
    }

    private void refresh() throws Exception {

        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(TOKEN_LOCATION)
                .setGrantType(GrantType.REFRESH_TOKEN)
                .setRefreshToken(refreshToken)
                .buildBodyMessage();

        request.setHeader("Authorization", "Basic " + new String(consumerAuth, StandardCharsets.UTF_8));

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthResourceResponse resource = oAuthClient.resource(request, OAuth.HttpMethod.POST, OAuthResourceResponse.class);

        AccessTokenResponse response = objectMapper.readValue(resource.getBody(), AccessTokenResponse.class);
        expiresSeconds = (System.currentTimeMillis() / 1000) + response.getExpiresIn();
        accessToken = response.getAccessToken();
        refreshToken = response.getRefreshToken();
        LOG.info("REFRESH: {}", response);
    }

    private void authorize() throws Exception {

        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(TOKEN_LOCATION)
                .setGrantType(GrantType.CLIENT_CREDENTIALS)
                .buildBodyMessage();

        request.setHeader("Authorization", "Basic " + new String(consumerAuth, StandardCharsets.UTF_8));

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthResourceResponse resource = oAuthClient.resource(request, OAuth.HttpMethod.POST, OAuthResourceResponse.class);

        AccessTokenResponse response = objectMapper.readValue(resource.getBody(), AccessTokenResponse.class);
        expiresSeconds = (System.currentTimeMillis() / 1000) + response.getExpiresIn();
        accessToken = response.getAccessToken();
        refreshToken = response.getRefreshToken();
        LOG.info("AUTH: {}", response);
    }

}
