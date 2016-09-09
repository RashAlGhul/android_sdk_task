package android.mobileconnect.gsma.com.library;

import android.mobileconnect.gsma.com.library.view.DiscoveryAuthenticationDialog;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;

import com.gsma.mobileconnect.r2.MobileConnectStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class DiscoveryWebViewClient extends MobileConnectWebViewClient
{
    private DiscoveryListener discoveryListener;

    private MobileConnectAndroidInterface mobileConnectAndroidInterface;

    public DiscoveryWebViewClient(final DiscoveryAuthenticationDialog dialog,
                                  final ProgressBar progressBar,
                                  final String redirectUrl,
                                  final DiscoveryListener discoveryListener,
                                  final MobileConnectAndroidInterface mobileConnectAndroidInterface)
    {
        super(dialog, progressBar, redirectUrl);
        this.discoveryListener = discoveryListener;
        this.mobileConnectAndroidInterface = mobileConnectAndroidInterface;
    }

    @Override
    protected boolean qualifyUrl(String url)
    {
        return url.contains("mcc_mnc=");
    }

    @Override
    protected void handleError(MobileConnectStatus status)
    {
        discoveryListener.discoveryFailed(status);
    }

    @Override
    protected void handleResult(String url)
    {
        DiscoveryModel.getInstance().setDiscoveryServiceRedirectedURL(url);
        Log.d("url", url);

        URI uri = getUri(url);

        mobileConnectAndroidInterface.handleRedirect(uri,
                                                     null,
                                                     UUID.randomUUID().toString(),
                                                     UUID.randomUUID().toString(),
                                                     new MobileConnectAndroidInterface.IMobileConnectCallback()
                                                     {
                                                         @Override
                                                         public void onComplete(MobileConnectStatus mobileConnectStatus)
                                                         {
                                                             discoveryListener.onDiscoveryResponse(mobileConnectStatus);
                                                         }
                                                     });

    }

    @Nullable
    private URI getUri(final String url)
    {
        URI uri;
        try
        {
            uri = new URI(url);
        }
        catch (URISyntaxException e)
        {
            uri = null;
            Log.e("Invalid Redirect URI", e.getMessage());
        }
        return uri;
    }
}