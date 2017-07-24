import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.demo.fragments.BaseAuthFragment;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;

public class BaseFragmentHeir extends BaseAuthFragment {

    public void handleRedirect(MobileConnectStatus status) {
        super.handleRedirect(status);
    }

    public synchronized void startAuthentication(@NonNull final MobileConnectStatus mobileConnectStatus,
                                                 @Nullable final MobileConnectRequestOptions mobileConnectRequestOptions,
                                                 @NonNull final String state,
                                                 @NonNull final String nonce) {
        super.startAuthentication(mobileConnectStatus, mobileConnectRequestOptions, state, nonce);

    }

    public void displayResult()  {
        super.displayResult();
    }

    @Override
    public void onComplete(DiscoveryResponse discoveryResponse) {

    }
}
