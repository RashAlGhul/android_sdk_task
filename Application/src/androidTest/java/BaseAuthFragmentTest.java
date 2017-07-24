import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentHostCallback;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Method;

/**
 * Created by e.dima on 18.7.17.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BaseAuthFragmentTest {
    @Mock
    private MobileConnectAndroidView mockAndroidView =
            Mockito.mock(MobileConnectAndroidView.class);
    @Mock
    private MobileConnectConfig mobileConnectConfig = Mockito.mock(MobileConnectConfig.class);
    @Mock
    private MobileConnectStatus mobileConnectStatus =
            Mockito.mock(MobileConnectStatus.class);

    private Bundle bundle = new Bundle();
    private Method[] baseAuthFragmentMethods;
    private BaseFragmentHeir heir = new BaseFragmentHeir();
    private String testString = "Help me, StackOverflow, you're my only hope!";

    /*@Rule
    public ActivityTestRule mActivityRule =
            new ActivityTestRule(BaseFragmentHeir.class,
                    false *//* Initial touch mode *//*, false *//*  launch activity *//*) {
                @Override
                protected void beforeActivityLaunched()
                {
                    heir = new BaseFragmentHeir();
                }
                @Override
                protected void afterActivityLaunched() {
                    heir = null;
                }
            };
*/
    @Rule
    public ActivityTestRule testRule = new ActivityTestRule(BaseFragmentHeir.class, true, true);

    @Test
    public synchronized void connectMobileWithoutDiscovery() throws Exception {
        /*testRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                heir = new BaseFragmentHeir();
                heir.connectMobileWithoutDiscovery();
                MobileConnectConfig newConfig = Mockito.when(new MobileConnectConfig.Builder().getClass()).getMock();
                newConfig.equals(mobileConnectConfig);
                mobileConnectAndroidView.cleanUp();
            }
        });*/
        /*ActivityTestRule mActivityRule = testRule;
        MobileConnectAndroidView mobileConnectAndroidView = mockAndroidView;
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mockAndroidView.cleanUp();
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        //authenticationSuccess(mobileConnectStatusAuthentication);
                        Mockito.verify(mockAndroidView).initialise()
                                .attemptAuthenticationWithWebView(mActivityRule.getActivity(),
                                        mockAuthenticationListener, url, state, nonce,
                                        mockRequestOptions);
                    }
                };
            }
        });*/
    }

    @Test
    public synchronized void connectMobileDemo() throws Exception {
        heir.connectMobileWithoutDiscovery();
        MobileConnectConfig newConfig = Mockito.when(new MobileConnectConfig.Builder().getClass()).getMock();
        newConfig.equals(mobileConnectConfig);
    }

    @Test
    public synchronized void onStart() throws Exception {
        ActivityTestRule mActivityRule = testRule;
        MobileConnectAndroidView mobileConnectAndroidView = mockAndroidView;
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                heir = new BaseFragmentHeir();
                mockAndroidView.cleanUp();
                heir.onStart();
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        //authenticationSuccess(mobileConnectStatus);
                        Mockito.verify(mockAndroidView).initialise();
                                /*.attemptAuthenticationWithWebView(mActivityRule.getActivity(),
                                        mockAuthenticationListener, url, state, nonce,
                                        mockRequestOptions);*/
                    }
                };
            }
        });
    }

    @Test
    public synchronized void onStop() throws Exception {
        //heir.onStart();
        //Mockito.verify(mockConnectAndroidView).isRegistered();
    }

    @Test
    public synchronized void handleRedirect() throws Exception {
        heir.handleRedirect(mobileConnectStatus);
        Mockito.verify(mobileConnectStatus).getResponseType();
    }

    @Test
    public synchronized void makeManualDiscovery() throws Exception {
        OperatorUrls urls = Mockito.mock(OperatorUrls.class);
        heir.makeManualDiscovery(testString,testString,testString,testString,testString,bundle);
        Mockito.verify(mobileConnectConfig).getRedirectUrl();
        //Mockito.verify(mobileConnectAndroidView).generateDiscoveryManually(testString, testString,
                //testString, testString, urls, heir);
    }

    @Test
    public synchronized void makeDiscoveryDemo() throws Exception {
        MobileConnectRequestOptions requestOptions =
                Mockito.mock(MobileConnectRequestOptions.class);
        heir.makeDiscoveryDemo(testString,testString);
        Mockito.verify(mobileConnectConfig).getRedirectUrl();
        //Mockito.verify(mobileConnectAndroidView).attemptDiscovery(testString,testString,testString,
                //requestOptions, heir);
    }

    @Test
    public synchronized void startAuthentication() throws Exception {
//        MobileConnectRequestOptions requestOptions =
//                Mockito.mock(MobileConnectRequestOptions.class);
//        heir.startAuthentication(mobileConnectStatus, requestOptions, testString, testString);
//        Mockito.verify(mobileConnectAndroidView).startAuthentication(testString, testString,
//                testString, requestOptions);
//        Mockito.verify(mobileConnectStatus).getDiscoveryResponse()
//                .getResponseData()
//                .getSubscriberId();
    }

    @Test
    public synchronized void displayResult() throws Exception {
        FragmentHostCallback callback = Mockito.mock(FragmentHostCallback.class);
        Intent intent = Mockito.mock(Intent.class);
        heir.displayResult();
        Mockito.verify(callback).onStartActivityFromFragment(heir,intent,-1,null);
    }

    @Test
    public synchronized void onDiscoveryResponse() throws Exception {
        heir.onDiscoveryResponse(mobileConnectStatus);
        Mockito.verify(mobileConnectStatus).getResponseType();
    }

    @Test
    public synchronized void discoveryFailed() throws Exception {
        heir.onDiscoveryResponse(mobileConnectStatus);
        Mockito.verify(mobileConnectStatus).getErrorMessage();
    }

    @Test
    public synchronized void onComplete() throws Exception {
        heir.onComplete(mobileConnectStatus);
        Mockito.verify(mobileConnectStatus).getResponseType();
    }


    @Test
    public synchronized void authenticationSuccess() throws Exception {
        heir.onAuthenticationDialogClose();
        Mockito.verify(mobileConnectStatus).getResponseType();
    }
}