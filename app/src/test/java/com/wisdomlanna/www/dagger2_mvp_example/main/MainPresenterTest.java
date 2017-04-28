package com.wisdomlanna.www.dagger2_mvp_example.main;

import com.wisdomlanna.www.dagger2_mvp_example.api.dao.UserInfoDao;
import com.wisdomlanna.www.dagger2_mvp_example.api.service.GitHubApi;
import com.wisdomlanna.www.dagger2_mvp_example.main.utils.JsonMockUtility;
import com.wisdomlanna.www.dagger2_mvp_example.main.utils.RxSchedulersOverrideRule;
import com.wisdomlanna.www.dagger2_mvp_example.ui.MainInterface;
import com.wisdomlanna.www.dagger2_mvp_example.ui.MainPresenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class MainPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule schedulers = new RxSchedulersOverrideRule();
    @Mock
    private MainInterface.View mockView;
    @Mock
    private GitHubApi gitHubApi;

    private MainPresenter presenter;
    private JsonMockUtility jsonUtil;
    private ResponseBody responseBody;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        responseBody = ResponseBody.create(MediaType.parse("application/json"), "");
        jsonUtil = new JsonMockUtility();

        presenter = new MainPresenter(gitHubApi);
        presenter.attachView(mockView);
        MainPresenter spyPresenter = spy(presenter);
        spyPresenter.attachView(mockView);
    }

    @After
    public void destroy() {
        presenter.detachView();
    }

    @Test
    public void plus() throws Exception {
        presenter.plus(5, 5);
        verify(mockView, times(1)).showProgressDialog();
        verify(mockView, times(1)).hideProgressDialog();
        verify(mockView, times(1)).showResultPlus(eq(10));
        assertThat(10, is(10));
    }

    @Test
    public void loadUserInfoGitHubSuccess() throws Exception {
        UserInfoDao mockResult = jsonUtil.getJsonToMock(
                "user_info_success.json",
                UserInfoDao.class);

        Response<UserInfoDao> mockResponse = Response.success(mockResult);
        Observable<Response<UserInfoDao>> mockCall = Observable.just(mockResponse);
        when(gitHubApi.getUserInfo(anyString())).thenReturn(mockCall);
        presenter.loadUserInfo("pondthaitay");
        verify(mockView, times(1)).showProgressDialog();
        TestObserver<Response<UserInfoDao>> testObserver =
                gitHubApi.getUserInfo(anyString()).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).hideProgressDialog();
            verify(mockView, times(1)).showResultUserInfoGitHubApi(eq(mockResult));
            assertThat(response, is(mockResponse));
            return true;
        });
    }

    @Test
    public void loadUserInfoGitHubError() throws Exception {
        Response<UserInfoDao> mockResponse = Response.error(500, responseBody);
        mockResponse.message();
        Observable<Response<UserInfoDao>> mockCall = Observable.just(mockResponse);
        when(gitHubApi.getUserInfo(anyString())).thenReturn(mockCall);
        presenter.loadUserInfo("pondthaitay");
        verify(mockView, times(1)).showProgressDialog();

        TestObserver<Response<UserInfoDao>> testObserver =
                gitHubApi.getUserInfo(anyString()).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).hideProgressDialog();
            verify(mockView, times(1)).showError(eq(response.message()));
            assertThat(response.message(), is(mockResponse.message()));
            return true;
        });
    }

    @Test
    public void loadUserInfoGitHubUnAuthorized() throws Exception {
        Response<UserInfoDao> mockResponse = Response.error(401, responseBody);
        Observable<Response<UserInfoDao>> mockCall = Observable.just(mockResponse);
        when(gitHubApi.getUserInfo(anyString())).thenReturn(mockCall);
        presenter.loadUserInfo("pondthaitay");
        verify(mockView, times(1)).showProgressDialog();

        TestObserver<Response<UserInfoDao>> testObserver =
                gitHubApi.getUserInfo(anyString()).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).hideProgressDialog();
            verify(mockView, times(1)).unAuthorizedApi();
            assertThat(response, is(mockResponse));
            return true;
        });
    }
}