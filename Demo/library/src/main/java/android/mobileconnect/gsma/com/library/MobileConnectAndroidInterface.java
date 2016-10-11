package android.mobileconnect.gsma.com.library;

import android.content.Context;
import android.content.DialogInterface;
import android.mobileconnect.gsma.com.library.callback.AuthenticationListener;
import android.mobileconnect.gsma.com.library.callback.AuthenticationWebViewCallback;
import android.mobileconnect.gsma.com.library.callback.DiscoveryListener;
import android.mobileconnect.gsma.com.library.callback.IMobileConnectOperation;
import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.mobileconnect.gsma.com.library.view.InteractableWebView;
import android.mobileconnect.gsma.com.library.webviewclient.AuthenticationWebViewClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class interfaces with the underlying Java SDK. It wraps calls to the Java SDK in
 * {@link AsyncTask}s and sends the result via a {@link IMobileConnectCallback}
 * <p/>
 * Created by usmaan.dad on 11/08/2016.
 */
//TODO this class is slowly being replaced by the MVP pattern in the 'main' package
public class MobileConnectAndroidInterface
{
    private final MobileConnectInterface mobileConnectInterface;

    private String mcc;

    private String mnc;

    private DiscoveryResponse discoveryResponse;

    /**
     * @param mobileConnectInterface The {@link MobileConnectConfig} containing the necessary set-up.
     */
    public MobileConnectAndroidInterface(@NonNull final MobileConnectInterface mobileConnectInterface)
    {
        this.mobileConnectInterface = mobileConnectInterface;
    }

    /**
     * Returns a cached DiscoveryResponse.
     *
     * @return The cached DiscoveryResponse
     */
    public DiscoveryResponse getDiscoveryResponse()
    {
        return this.discoveryResponse;
    }

    /**
     * Launches a Dialog containing a {@link WebView} and loads the passed-in operatorUrl.
     *
     * @param activityContext   The {@link Context} in which the Dialog is used to draw
     * @param discoveryListener The callback determining success, failure or the dialog being closed
     * @param operatorUrl       The url to load in to the {@link WebView}
     * @param redirectUrl       The redirect url expected to be received from the API
     */
    public void attemptDiscoveryWithWebView(final Context activityContext,
                                            final DiscoveryListener discoveryListener,
                                            final String operatorUrl,
                                            final String redirectUrl,
                                            final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        initiateWebView(activityContext, discoveryListener, operatorUrl, redirectUrl, mobileConnectRequestOptions);
    }

    public void attemptAuthenticationWithWebView(@NonNull final Context activityContext,
                                                 @NonNull final AuthenticationListener authenticationListener,
                                                 @NonNull final String url,
                                                 @NonNull final String state,
                                                 @NonNull String nonce,
                                                 @Nullable final MobileConnectRequestOptions
                                                         mobileConnectRequestOptions)
    {
        initiateWebView(activityContext, authenticationListener, url, state, nonce, mobileConnectRequestOptions);
    }

    private void initiateWebView(@NonNull final Context activityContext,
                                 @NonNull final DiscoveryListener discoveryListener,
                                 @NonNull final String operatorUrl,
                                 @NonNull final String redirectUrl,
                                 final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        RelativeLayout webViewLayout = (RelativeLayout) LayoutInflater.from(activityContext)
                                                                      .inflate(R.layout.layout_web_view, null);

        final InteractableWebView webView = (InteractableWebView) webViewLayout.findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) webViewLayout.findViewById(R.id.progressBar);

        final DiscoveryAuthenticationDialog dialog = new DiscoveryAuthenticationDialog(activityContext);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                Log.e("Discovery Dialog", "cancelled");
                closeWebViewAndNotify(discoveryListener, webView);
            }
        });

        dialog.setContentView(webViewLayout);

        webView.setWebChromeClient(new WebChromeClient());

        //        final DiscoveryWebViewClient webViewClient = new DiscoveryWebViewClient(dialog,
        //                                                                                progressBar,
        //                                                                                redirectUrl,
        //                                                                                this,
        //                                                                                mobileConnectRequestOptions);
        //        webView.setWebViewClient(webViewClient);

        webView.loadUrl(operatorUrl);

        try
        {
            dialog.show();
        }
        catch (final WindowManager.BadTokenException exception)
        {
            Log.e("Discovery Dialog", exception.getMessage());
        }
    }

    private void initiateWebView(@NonNull final Context activityContext,
                                 @NonNull final AuthenticationListener authenticationListener,
                                 @NonNull final String authenticationUrl,
                                 @NonNull final String state,
                                 @NonNull final String nonce,
                                 final MobileConnectRequestOptions mobileRequestOptions)
    {
        RelativeLayout webViewLayout = (RelativeLayout) LayoutInflater.from(activityContext)
                                                                      .inflate(R.layout.layout_web_view, null);

        final InteractableWebView webView = (InteractableWebView) webViewLayout.findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) webViewLayout.findViewById(R.id.progressBar);

        final DiscoveryAuthenticationDialog dialog = new DiscoveryAuthenticationDialog(activityContext);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                Log.e("Authentication Dialog", "cancelled");
                closeWebViewAndNotify(authenticationListener, webView);
            }
        });

        dialog.setContentView(webViewLayout);

        webView.setWebChromeClient(new WebChromeClient());

        Uri uri = Uri.parse(authenticationUrl);
        String redirectUrl = uri.getQueryParameter("redirect_uri");

        final AuthenticationWebViewClient webViewClient = new AuthenticationWebViewClient(dialog,
                                                                                          progressBar,
                                                                                          authenticationListener,
                                                                                          redirectUrl,
                                                                                          new AuthenticationWebViewCallback()
                                                                                          {
                                                                                              @Override
                                                                                              public void onSuccess(
                                                                                                      String url)
                                                                                              {
                                                                                                  handleRedirectAfterAuthentication(
                                                                                                          url,
                                                                                                          state,
                                                                                                          nonce,
                                                                                                          authenticationListener,
                                                                                                          mobileRequestOptions);
                                                                                              }
                                                                                          });

        webView.setWebViewClient(webViewClient);

        webView.loadUrl(authenticationUrl);

        try
        {
            dialog.show();
        }
        catch (final WindowManager.BadTokenException exception)
        {
            Log.e("Discovery Dialog", exception.getMessage());
        }
    }

    private void handleRedirectAfterAuthentication(final String url,
                                                   final String state,
                                                   final String nonce,
                                                   final AuthenticationListener authenticationListener,
                                                   final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        URI uri = null;

        try
        {
            uri = new URI(url);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        if (uri == null)
        {
            return;
        }

        handleUrlRedirect(uri, state, nonce, new IMobileConnectCallback()
        {
            @Override
            public void onComplete(MobileConnectStatus mobileConnectStatus)
            {
                authenticationListener.authorizationSuccess(mobileConnectStatus);
            }
        }, mobileConnectRequestOptions);
    }

    /**
     * Stop any processing on the {@link WebView} and notify that it has been stopped. This is called regardless of
     * success, failure or if the user has intentionally closed the dialog.
     *
     * @param listener The callback to notify of the closing of the Dialog
     * @param webView  The view to stop loading.
     */
    private void closeWebViewAndNotify(final DiscoveryListener listener, final WebView webView)
    {
        webView.stopLoading();
        webView.loadData("", "text/html", null);
        listener.onDiscoveryDialogClose();
    }

    /**
     * Stop any processing on the {@link WebView} and notify that it has been stopped. This is called regardless of
     * success, failure or if the user has intentionally closed the dialog.
     *
     * @param listener The callback to notify of the closing of the Dialog
     * @param webView  The view to stop loading.
     */
    private void closeWebViewAndNotify(final AuthenticationListener listener, final WebView webView)
    {
        webView.stopLoading();
        webView.loadData("", "text/html", null);
        listener.onAuthorizationDialogClose();
    }

    /**
     * Asynchronously attempt discovery using the supplied parameters. If msisdn, mcc and mnc are null the result
     * will be operator selection, otherwise valid parameters will result in a StartAuthorization
     * status
     *
     * @param mobileConnectCallback The callback in which a {@link MobileConnectStatus} shall be provided after
     *                              completion
     * @param msisdn                MSISDN from user
     * @param mcc                   Mobile Country Code
     * @param mnc                   Mobile Network Code
     * @param options               Optional parameters
     *                              Connect process
     */
    @SuppressWarnings("unused")
    public void attemptDiscovery(final String msisdn,
                                 final String mcc,
                                 final String mnc,
                                 final MobileConnectRequestOptions options,
                                 @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.attemptDiscovery(msisdn,
                                                                                                  mcc,
                                                                                                  mnc,
                                                                                                  options);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Asynchronously attempt discovery using the values returned from the operator selection redirect
     * <p/>
     *
     * @param mobileConnectCallback The callback in which a {@link MobileConnectStatus} shall be provided after
     *                              completion
     * @param redirectUri           URI redirected to by the completion of the operator selection UI
     *                              Connect process
     */
    @SuppressWarnings("unused")
    public void attemptDiscoveryAfterOperatorSelection(@NonNull final IMobileConnectCallback mobileConnectCallback,
                                                       @Nullable final URI redirectUri)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.attemptDiscoveryAfterOperatorSelection(
                        redirectUri);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Asynchronously creates an authorization url with parameters to begin the authorization process
     *
     * @param mobileConnectCallback The callback in which a {@link MobileConnectStatus} shall be provided after
     *                              completion
     * @param encryptedMSISDN       Encrypted MSISDN/Subscriber Id returned from the Discovery process
     * @param state                 Unique state value, this will be returned by the authorization
     *                              process and should be checked for equality as a secURIty measure
     * @param nonce                 Unique value to associate a client session with an id token
     * @param options               Optional parameters
     *                              Connect process
     */
    @SuppressWarnings("unused")
    public void startAuthentication(final String encryptedMSISDN,
                                    final String state,
                                    final String nonce,
                                    final MobileConnectRequestOptions options,
                                    @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.startAuthentication(discoveryResponse,
                                                                                                     encryptedMSISDN,
                                                                                                     state,
                                                                                                     nonce,
                                                                                                     options);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Request token using the values returned from the authorization redirect
     *
     * @param redirectedUrl               URI redirected to by the completion of the authorization UI
     * @param expectedState               The state value returned from the StartAuthorization call should be
     *                                    passed here, it will be used to validate the authenticity of the
     *                                    authorization process
     * @param expectedNonce               The nonce value returned from the StartAuthorization call should be
     *                                    passed here, it will be used to ensure the token was not requested
     *                                    using a replay attack
     * @param mobileConnectRequestOptions Optional parameters
     *                                    Connect process
     */
    @SuppressWarnings("unused")
    public void requestToken(final URI redirectedUrl,
                             final String expectedState,
                             final String expectedNonce,
                             @NonNull final IMobileConnectCallback mobileConnectCallback,
                             final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.requestToken(discoveryResponse,
                                                                                              redirectedUrl,
                                                                                              expectedState,
                                                                                              expectedNonce,
                                                                                              mobileConnectRequestOptions);
                // same here
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Handles continuation of the process following a completed redirect. Only the redirectedUrl is
     * required, however if the redirect being handled is the result of calling the Authorization
     * URL then the remaining parameters are required.
     *
     * @param redirectedUrl               Url redirected to by the completion of the previous step
     * @param expectedState               The state value returned from the StartAuthorization call should be
     *                                    passed here, it will be used to validate the authenticity of the
     *                                    authorization process
     * @param expectedNonce               The nonce value returned from the StartAuthorization call should be
     *                                    passed here, it will be used to ensure the token was not requested
     *                                    using a replay attack
     * @param mobileConnectRequestOptions Optional parameters
     *                                    Connect process
     */
    @SuppressWarnings("unused")
    public void handleUrlRedirect(final URI redirectedUrl,
                                  final String expectedState,
                                  final String expectedNonce,
                                  @NonNull final IMobileConnectCallback mobileConnectCallback,
                                  final MobileConnectRequestOptions mobileConnectRequestOptions)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.handleUrlRedirect(redirectedUrl,
                                                                                                   discoveryResponse,
                                                                                                   expectedState,
                                                                                                   expectedNonce,
                                                                                                   mobileConnectRequestOptions);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Request user info using the access token returned by <see cref="requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param accessToken Access token from requestToken stage
     */
    @SuppressWarnings("unused")
    public void requestIdentity(final String accessToken, @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.requestIdentity(discoveryResponse,
                                                                                                 accessToken);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Refresh token using using the refresh token provided in the RequestToken response
     *
     * @param refreshToken Refresh token returned from RequestToken request
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    @SuppressWarnings("unused")
    public void refreshToken(final String refreshToken, @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.refreshToken(refreshToken,
                                                                                              discoveryResponse);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Revoke token using using the access / refresh token provided in the RequestToken response
     *
     * @param token         Access/Refresh token returned from RequestToken request
     * @param tokenTypeHint Hint to indicate the type of token being passed in
     * @return MobileConnectStatus Object with required information for continuing the mobile
     * connect process
     */
    @SuppressWarnings("unused")
    public void revokeToken(final String token,
                            final String tokenTypeHint,
                            @NonNull final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.revokeToken(token,
                                                                                             tokenTypeHint,
                                                                                             discoveryResponse);
            }
        }, mobileConnectCallback).execute();
    }

    /**
     * Request user info using the access token returned by <see cref="requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param accessToken Access token from requestToken stage
     */
    public void requestUserInfo(final String accessToken, final IMobileConnectCallback mobileConnectCallback)
    {
        new MobileConnectAsyncTask(new IMobileConnectOperation()
        {
            @Override
            public MobileConnectStatus operation()
            {
                return MobileConnectAndroidInterface.this.mobileConnectInterface.requestUserInfo(discoveryResponse,
                                                                                                 accessToken);
            }
        }, mobileConnectCallback).execute();
    }

    public String getMcc()
    {
        return mcc;
    }

    public void setMcc(final String mcc)
    {
        this.mcc = mcc;
    }

    public void setMnc(final String mnc)
    {
        this.mnc = mnc;
    }

    public String getMnc()
    {
        return mnc;
    }

    /**
     * An AsyncTask to wrap all API calls to {@link MobileConnectInterface} asynchronously.
     */
    private class MobileConnectAsyncTask extends AsyncTask<Void, Void, MobileConnectStatus>
    {
        private IMobileConnectOperation mobileConnectOperation;

        private IMobileConnectCallback IMobileConnectCallback;

        public MobileConnectAsyncTask(@NonNull final IMobileConnectOperation mobileConnectOperation,
                                      @NonNull final IMobileConnectCallback IMobileConnectCallback)
        {

            this.mobileConnectOperation = mobileConnectOperation;
            this.IMobileConnectCallback = IMobileConnectCallback;
        }

        @Override
        protected MobileConnectStatus doInBackground(@Nullable final Void... voids)
        {
            return mobileConnectOperation.operation();
        }

        @Override
        protected void onPostExecute(final MobileConnectStatus mobileConnectStatus)
        {
            super.onPostExecute(mobileConnectStatus);
            if (IMobileConnectCallback != null)
            {
                if (mobileConnectStatus.getDiscoveryResponse() != null &&
                    !mobileConnectStatus.getDiscoveryResponse().hasExpired())
                {
                    discoveryResponse = mobileConnectStatus.getDiscoveryResponse();
                }

                IMobileConnectCallback.onComplete(mobileConnectStatus);
            }
        }
    }

    public interface IMobileConnectCallback
    {
        void onComplete(final MobileConnectStatus mobileConnectStatus);
    }
}