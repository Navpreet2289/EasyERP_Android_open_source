package com.thinkmobiles.easyerp.presentation.screens.home;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.thinkmobiles.easyerp.R;
import com.thinkmobiles.easyerp.data.model.user.UserInfo;
import com.thinkmobiles.easyerp.domain.UserRepository;
import com.thinkmobiles.easyerp.presentation.EasyErpApplication;
import com.thinkmobiles.easyerp.presentation.base.BaseMasterFlowActivity;
import com.thinkmobiles.easyerp.presentation.custom.views.drawer_menu.IMenuClickListener;
import com.thinkmobiles.easyerp.presentation.custom.views.drawer_menu.MenuDrawerContainer;
import com.thinkmobiles.easyerp.presentation.custom.views.drawer_menu.MenuDrawerState;
import com.thinkmobiles.easyerp.presentation.custom.views.drawer_menu.MenuDrawerView;
import com.thinkmobiles.easyerp.presentation.custom.views.drawer_menu.models.MenuConfigs;
import com.thinkmobiles.easyerp.presentation.dialogs.ChangePasswordDialogFragment;
import com.thinkmobiles.easyerp.presentation.dialogs.ChangePasswordDialogFragment_;
import com.thinkmobiles.easyerp.presentation.dialogs.UserProfileDialogFragment;
import com.thinkmobiles.easyerp.presentation.dialogs.UserProfileDialogFragment_;
import com.thinkmobiles.easyerp.presentation.managers.CookieManager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Lynx on 1/13/2017.
 */

@EActivity(R.layout.activity_home)
public class HomeActivity extends BaseMasterFlowActivity implements HomeContract.HomeView, IMenuClickListener,
        UserProfileDialogFragment.IUserProfileCallback, ChangePasswordDialogFragment.IChangePasswordCallback {

    private HomeContract.HomePresenter presenter;

    @Bean
    protected UserRepository userRepository;

    @Bean
    protected CookieManager cookieManager;

    @Extra
    protected UserInfo userInfo;

    @ViewById(R.id.mdvMenu_AH)
    protected MenuDrawerView menuDrawerView;

    @ViewById(R.id.mdvMenuContainer_AH)
    protected MenuDrawerContainer menuDrawerContainer;

    private ProgressDialog progressDialog;

    @AfterInject
    @Override
    public void initPresenter() {
        new HomePresenter(this, userRepository, cookieManager);
    }

    @Override
    public void setPresenter(HomeContract.HomePresenter presenter) {
        this.presenter = presenter;
    }

    @AfterViews
    protected void initMenu() {
        menuDrawerView.setMenuClickListener(this);
        menuDrawerView.setHeaderUserData(userInfo);
    }

    @Override
    protected void logOut() {
        presenter.logOut();
    }

    @Override
    protected void onHomeMenuSelect(boolean isHamburger) {
        if (isHamburger)
            changeStateMenu();
        else onBackPressed();
    }

    @Override
    protected int contentIdLayout() {
        return R.id.flContentContainer;
    }

    @Override
    protected int contentDetailIdLayout() {
        return R.id.flContentDetailContainer;
    }

    private void changeStateMenu() {
        switch (menuDrawerContainer.getStateMenu()) {
            case OPEN:
                menuDrawerContainer.setStateMenu(MenuDrawerState.CLOSE);
                break;
            case CLOSE:
                menuDrawerContainer.setStateMenu(MenuDrawerState.OPEN);
                break;
        }
        menuDrawerContainer.initStateMenu(false);
    }

    @Override
    public void chooseModule(int moduleId) {
        //TODO for analytics. Use MenuConfigs.getModuleLabel(moduleId); for tracking label the module
    }

    @Override
    public void chooseItem(int moduleId, int itemId) {
        replaceFragmentContent(MenuConfigs.getFragmentByMenuId(moduleId, itemId), MenuConfigs.getItemLabel(moduleId, itemId));
    }

    @Override
    public void onClickUser() {
        UserProfileDialogFragment_.builder().userInfo(userInfo).build().show(getFragmentManager(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        menuDrawerView.defaultSelect();
    }

    @Override
    public void restartApp() {
        EasyErpApplication.getInstance().restartApp();
    }

    @Override
    public void showProgress(String msg) {
        progressDialog = new ProgressDialog(this, R.style.DefaultTheme_NoTitleDialogWithAnimation);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void showErrorToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInfoToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe();
    }

    @Override
    public void showChangeProfile() {
        ChangePasswordDialogFragment_.builder().build().show(getFragmentManager(), null);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        presenter.changePassword(userInfo.id, oldPassword, newPassword);
    }
}
