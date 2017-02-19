package com.internship.pbt.bizarechat.presentation.view.activity;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.internship.pbt.bizarechat.R;
import com.internship.pbt.bizarechat.data.repository.SessionDataRepository;
import com.internship.pbt.bizarechat.domain.events.FirebaseTokenRefreshCompleteEvent;
import com.internship.pbt.bizarechat.domain.events.GcmMessageReceivedEvent;
import com.internship.pbt.bizarechat.domain.interactor.SignOutUseCase;
import com.internship.pbt.bizarechat.presentation.BizareChatApp;
import com.internship.pbt.bizarechat.presentation.navigation.Navigator;
import com.internship.pbt.bizarechat.presentation.presenter.main.MainPresenterImpl;
import com.internship.pbt.bizarechat.presentation.view.fragment.friends.InviteFriendsFragment;
import com.internship.pbt.bizarechat.presentation.view.fragment.newchat.NewChatFragment;
import com.internship.pbt.bizarechat.presentation.view.fragment.privateChat.PrivateChatFragment;
import com.internship.pbt.bizarechat.presentation.view.fragment.publicChat.PublicChatFragment;
import com.internship.pbt.bizarechat.presentation.view.fragment.users.UsersFragment;
import com.internship.pbt.bizarechat.service.messageservice.BizareChatMessageService;
import com.internship.pbt.bizarechat.service.messageservice.MessageServiceBinder;
import com.internship.pbt.bizarechat.service.util.NotificationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends MvpAppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, MainView {
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean dialogsExist = false;

    private final String newChatFragmentTag = "newChatFragment";
    private final String usersFragmentTag = "usersFragment";
    private final static String INVITE_FRIENDS_FR_TAG = "inviteFriendsFragment";

    @InjectPresenter
    MainPresenterImpl presenter;

    @ProvidePresenter
    MainPresenterImpl provideMainPresenter() {
        return new MainPresenterImpl(new SignOutUseCase(new SessionDataRepository(BizareChatApp.
                getInstance().getSessionService())));
    }

    private RelativeLayout mLayout;
    private TextView mTextOnToolbar;
    private NavigationView mNavigationView;
    private Navigator navigator = Navigator.getInstance();
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private AppBarLayout.LayoutParams toolbarParams;
    private TabLayout mTabLayout;
    private FloatingActionButton fab;
    private ActionBarDrawerToggle toggle;
    private BizareChatMessageService messageService;
    private ServiceConnection messageServiceConnection;
    private Intent messageServiceIntent;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_base_layout);
        messageServiceIntent = new Intent(this, BizareChatMessageService.class);

        findViews();
        setToolbarAndNavigationDrawer();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void setToolbarAndNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.setToolbarNavigationClickListener(this);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_screen_container, new PublicChatFragment())
                            .commit();
                }
                if (tab.getPosition() == 1) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_screen_container, new PrivateChatFragment())
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void findViews() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        mLayout = (RelativeLayout) findViewById(R.id.layout_chat_empty);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("Public"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Private"));
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        setSupportActionBar(mToolbar);
        mTextOnToolbar = (TextView) findViewById(R.id.chat_toolbar_title);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
    }

    @Override
    public void showInviteFriendsScreen() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(INVITE_FRIENDS_FR_TAG);
        if (fragment != null) {
            transaction.replace(R.id.main_screen_container, fragment, INVITE_FRIENDS_FR_TAG)
                    .commit();
            return;
        }

        transaction.replace(R.id.main_screen_container, new InviteFriendsFragment(),
                INVITE_FRIENDS_FR_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START, true);
        switch (item.getItemId()) {
            case R.id.create_new_chat:
                presenter.addNewChat();
                return true;
            case R.id.users:
                presenter.navigateToUsers();
                return true;
            case R.id.log_out:
                presenter.confirmLogOut();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void confirmLogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle(R.string.are_you_sure);
        builder.setPositiveButton(R.string.sign_out, (dialog1, whichButton) -> {

        });

        builder.setNegativeButton(R.string.cancel, (dialog12, whichButton) -> dialog12.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
        Button buttonSignOut = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        buttonSignOut.setOnClickListener(
                v -> presenter.logout()
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                mNavigationView.getMenu().getItem(0).setChecked(true);
                presenter.addNewChat();
                break;
            case -1:
                onBackPressed();
                break;
            case R.id.invite_friends:
                presenter.inviteFriends();

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void showLackOfFriends() {
        Snackbar.make(mDrawer, R.string.no_friends_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void startNewChatView() {
        if(presenter.isInRestoreState(this)){
            if(getSupportFragmentManager().getBackStackEntryCount() > 1)
                getSupportFragmentManager().popBackStack();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(newChatFragmentTag);
        if(fragment != null){
            getSupportFragmentManager().popBackStack();
            transaction.replace(R.id.main_screen_container, fragment, newChatFragmentTag)
                    .commit();
            return;
        }

        transaction.replace(R.id.main_screen_container, new NewChatFragment(), newChatFragmentTag)
                .addToBackStack(newChatFragmentTag)
                .commit();
    }

    @Override
    public void startUsersView() {
        if(presenter.isInRestoreState(this)){
            if(getSupportFragmentManager().getBackStackEntryCount() > 1)
                getSupportFragmentManager().popBackStack();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(usersFragmentTag);
        if(fragment != null){
            getSupportFragmentManager().popBackStack();
            transaction.replace(R.id.main_screen_container, fragment, usersFragmentTag)
                    .commit();
            return;
        }

        transaction.replace(R.id.main_screen_container, new UsersFragment(), usersFragmentTag)
                .addToBackStack(newChatFragmentTag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void startBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            showNavigationElements();
        }
        super.onBackPressed();
    }

    private void startActionBarToggleAnim(float start, float end) {
        ValueAnimator anim = ValueAnimator.ofFloat(start, end);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                toggle.onDrawerSlide(null, slideOffset);

                if (slideOffset == 1 && start == 0) {
                    toggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(200);
        anim.start();
    }

    @Override
    public void showNavigationElements() {
        fab.show();
        mTextOnToolbar.setText(getString(R.string.chat));
        mNavigationView.getMenu().getItem(0).setChecked(true);
        mNavigationView.getMenu().getItem(0).setChecked(false);
        toolbarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        mTabLayout.setVisibility(View.VISIBLE);
        if (!dialogsExist)
            mLayout.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(true);
        startActionBarToggleAnim(1, 0);
    }

    @Override
    public void hideNavigationElements() {
        fab.hide();
        toolbarParams.setScrollFlags(0);
        mTabLayout.setVisibility(View.GONE);
        mLayout.setVisibility(View.GONE);
        startActionBarToggleAnim(0, 1);
    }


    @Override
    public void navigateToLoginScreen() {
        navigator.navigateToLoginActivity(this);
    }

    @SuppressWarnings("unchecked")
    private void bindMessageService(){
        messageServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                messageService = ((MessageServiceBinder<BizareChatMessageService>)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                messageService = null;
            }
        };
        bindService(messageServiceIntent, messageServiceConnection, 0);
    }

    private void unbindMessageService(){
        unbindService(messageServiceConnection);
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onFirebaseTokenRefreshComplete(FirebaseTokenRefreshCompleteEvent event){
        Log.d(TAG, event.getRefreshedToken());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onGsmMessageReceived(GcmMessageReceivedEvent event){
        Log.d(TAG, event.getMessage());
    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationUtils.clearNotifications(getApplicationContext());
        EventBus.getDefault().register(this);
//        bindMessageService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
//        unbindMessageService();
    }
}