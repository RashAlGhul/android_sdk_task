import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.android.demo.activity.MainActivity;
import com.gsma.mobileconnect.r2.android.main.MobileConnectAndroidView;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by e.dima on 18.7.17.
 */
@RunWith(AndroidJUnit4.class)
public class BaseAuthFragmentTest {
    @Mock
    private MobileConnectAndroidView mockAndroidView =
            Mockito.mock(MobileConnectAndroidView.class);
    @Mock
    private MobileConnectConfig mockConfig = Mockito.mock(MobileConnectConfig.class);
    @Mock
    private MobileConnectStatus mockStatus =
            Mockito.mock(MobileConnectStatus.class);
    @Mock
    private MobileConnectRequestOptions mockOptions =
            Mockito.mock(MobileConnectRequestOptions.class);
    @Mock
    private DiscoveryResponse mockResponse = Mockito.mock(DiscoveryResponse.class);
    private Bundle bundle = new Bundle();
    private BaseFragmentHeir heir = new BaseFragmentHeir();
    private String testString = "Help me, StackOverflow, you're my only hope!";

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setUp(){
        Mockito.reset(mockStatus);
        Mockito.reset(mockConfig);
        Mockito.reset(mockAndroidView);
        Mockito.reset(mockOptions);
        mockAndroidView.cleanUp();
    }

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
        newConfig.equals(mockConfig);
    }

    @Test
    public synchronized void onStart() throws Exception {
        mActivityRule.getActivity().runOnUiThread(new Runnable() {            @Override            public void run() {
            synchronized(this) {
                BaseFragmentHeir heir = spy(BaseFragmentHeir.class);
                doNothing().when(heir).onStart();
                heir.onStart();
                verify(heir).onStart();
                verify(mockAndroidView).setIsRegistered(true);
                this.notify();
            }
        }        });
    }

    @Test
    public synchronized void onStop() throws Exception {
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            synchronized(this) {
                BaseFragmentHeir heir = spy(BaseFragmentHeir.class);
                doNothing().when(heir).onStop();
                heir.onStop();
                verify(heir).onStop();
                verify(mockAndroidView).setIsRegistered(false);
                this.notify();
            }
        }        });
    }

    @Test
    public synchronized void handleRedirect() throws Exception {
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            synchronized(this) {
                BaseFragmentHeir heir = spy(BaseFragmentHeir.class);
                MobileConnectStatus status = mock(MobileConnectStatus.class);
                doNothing().when(heir).handleRedirect(mockStatus);
                heir.handleRedirect(status);
                verify(status).getResponseType();
                this.notify();
            }
        }
        });

    }

    @Test
    public synchronized void makeManualDiscovery() throws Exception {
        mActivityRule.getActivity().runOnUiThread(new Runnable() {            @Override            public void run() {
            synchronized(this) {
                BaseFragmentHeir heir = spy(BaseFragmentHeir.class);
                OperatorUrls urls = Mockito.mock(OperatorUrls.class);
                doNothing().when(heir).makeManualDiscovery(testString, testString,
                        testString, testString, testString, bundle);
                heir.makeManualDiscovery(testString, testString,
                        testString, testString, testString, bundle);
                verify(mockAndroidView).generateDiscoveryManually(testString, testString,
                        testString, testString, urls, heir);
                this.notify();
            }
        }        });
    }

    @Test
    public synchronized void makeDiscoveryDemo() throws Exception {
        mActivityRule.getActivity().runOnUiThread(new Runnable() {            @Override            public void run() {
            synchronized(this) {
                BaseFragmentHeir heir = spy(BaseFragmentHeir.class);
                doNothing().when(heir).makeDiscoveryDemo(testString, testString);
                heir.makeDiscoveryDemo(testString, testString);
                /*verify(mockAndroidView).attemptDiscovery(testString,
                        null,
                        null,
                        testString,
                        BaseFragmentHeir.this);*/
                this.notify();
            }
        }        });
    }

    @Test//
    public synchronized void startAuthentication() throws Exception {
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                BaseFragmentHeir heir = spy(BaseFragmentHeir.class);
                //mockStatus = spy(MobileConnectStatus.class);
                //mockOptions = Mockito.mock(MobileConnectRequestOptions.class);
                doNothing().when(heir).startAuthentication(
                        mockStatus,mockOptions,testString,testString);
                heir.startAuthentication(
                        mockStatus,mockOptions,testString,testString);
                verify(heir).startAuthentication(
                        mockStatus,mockOptions,testString,testString);
                /*verify(mockAndroidView).startAuthentication(mockStatus.getDiscoveryResponse()
                                .getResponseData()
                                .getSubscriberId(),
                        testString,
                        testString,
                        mockOptions,
                        new IMobileConnectContract.IMobileConnectCallback() {
                            @Override
                            public void onComplete(final MobileConnectStatus
                                                           mobileConnectStatus) {
                                //handleRedirect(mockStatus);
                            }
                        });*/
                this.notify();
            }
        }        });
    }

    @Test
    public synchronized void displayResult() throws Exception {
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    BaseFragmentHeir heir = spy(BaseFragmentHeir.class);
                    doNothing().when(heir).displayResult();
                    heir.onStop();
                    verify(heir).onStop();
                    this.notify();
                }
            }
        });
    }

    @Test//
    public synchronized void onDiscoveryResponse() throws Exception {
        //heir.onDiscoveryResponse(discoveryFailed(mockStatus);
        verify(mockStatus).getResponseType();
    }

    @Test//?
    public synchronized void discoveryFailed() throws Exception {
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized(this) {
                    BaseFragmentHeir heir = spy(BaseFragmentHeir.class);
                    doNothing().when(heir).discoveryFailed(mockStatus);
                    heir.discoveryFailed(mockStatus);
                    verify(heir).discoveryFailed(mockStatus);
                    //verify(mockStatus).getErrorMessage().toString();
                    this.notify();
                }
            }        });
    }

    @Test//
    public synchronized void onComplete() throws Exception {
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized(this) {
                    BaseFragmentHeir heir = spy(BaseFragmentHeir.class);
                    doNothing().when(heir).onComplete(mockStatus);
                    heir.onComplete(mockStatus);
                    verify(heir).onComplete(mockStatus);
                    //verify(heir).displayResult();
                    this.notify();
                }
            }        });
    }

    @Test//
    public synchronized void authenticationSuccess() throws Exception {
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized(this) {
                    BaseFragmentHeir heir = spy(BaseFragmentHeir.class);
                    doNothing().when(heir).authenticationSuccess(mockStatus);
                    heir.authenticationSuccess(mockStatus);
                    verify(heir).authenticationSuccess(mockStatus);
                    verify(heir).handleRedirect(mockStatus);
                    this.notify();
                }
            }        });
    }
}