package com.thinkmobiles.easyerp.presentation.screens.crm.companies.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.thinkmobiles.easyerp.R;
import com.thinkmobiles.easyerp.domain.crm.CompaniesRepository;
import com.thinkmobiles.easyerp.presentation.adapters.crm.ContactsAdapter;
import com.thinkmobiles.easyerp.presentation.adapters.crm.HistoryAdapter;
import com.thinkmobiles.easyerp.presentation.adapters.crm.OpportunityAndLeadsAdapter;
import com.thinkmobiles.easyerp.presentation.base.rules.RefreshFragment;
import com.thinkmobiles.easyerp.presentation.base.rules.ErrorViewHelper;
import com.thinkmobiles.easyerp.presentation.custom.transformations.CropCircleTransformation;
import com.thinkmobiles.easyerp.presentation.holders.data.crm.ContactDH;
import com.thinkmobiles.easyerp.presentation.holders.data.crm.HistoryDH;
import com.thinkmobiles.easyerp.presentation.holders.data.crm.OpportunityAndLeadDH;
import com.thinkmobiles.easyerp.presentation.managers.HistoryAnimationHelper;
import com.thinkmobiles.easyerp.presentation.managers.ImageHelper;
import com.thinkmobiles.easyerp.presentation.screens.home.HomeActivity;
import com.thinkmobiles.easyerp.presentation.utils.Constants;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lynx on 2/2/2017.
 */

@EFragment
public class CompanyDetailsFragment extends RefreshFragment implements CompanyDetailsContract.CompanyDetailsView {

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_company_details;
    }

    private CompanyDetailsContract.CompanyDetailsPresenter presenter;

    @FragmentArg
    protected String companyID;

    //region View inject
    @ViewById
    protected NestedScrollView nsvContent_FCD;
    @ViewById
    protected ImageView ivCompanyAvatar_FCD;
    @ViewById
    protected TextView tvCompanyName_FCD;
    @ViewById
    protected TextView tvCompanyWebsite_FCD;
    @ViewById
    protected ImageView ivCompanyFb_FCD;
    @ViewById
    protected ImageView ivCompanyLinkedIn_FCD;
    @ViewById
    protected ImageView ivCompanySkype_FCD;
    @ViewById
    protected EditText etEmail_FCD;
    @ViewById
    protected EditText etAssignedTo_FCD;
    @ViewById
    protected EditText etLinkedIn_FCD;
    @ViewById
    protected EditText etFacebook_FCD;
    @ViewById
    protected EditText etPhone_FCD;
    @ViewById
    protected EditText etMobile_FCD;
    @ViewById
    protected EditText etBillingStreet_FCD;
    @ViewById
    protected EditText etBillingCity_FCD;
    @ViewById
    protected EditText etBillingState_FCD;
    @ViewById
    protected EditText etBillingZip_FCD;
    @ViewById
    protected EditText etBillingCountry_FCD;
    @ViewById
    protected EditText etShippingFullname_FCD;
    @ViewById
    protected EditText etShippingStreet_FCD;
    @ViewById
    protected EditText etShippingCity_FCD;
    @ViewById
    protected EditText etShippingState_FCD;
    @ViewById
    protected EditText etShippingZip_FCD;
    @ViewById
    protected EditText etShippingCountry_FCD;
    @ViewById
    protected EditText etReference_FCD;
    @ViewById
    protected CheckBox cbIsCustomer_FCD;
    @ViewById
    protected CheckBox cbIsSupplier_FCD;
    @ViewById
    protected EditText etSalesTeam_FCD;
    @ViewById
    protected EditText etSalesPerson_FCD;
    @ViewById
    protected EditText etImplementedBy_FCD;
    @ViewById
    protected EditText etLanguage_FCD;
    @ViewById
    protected RecyclerView rvContacts_FCD;
    @ViewById
    protected RecyclerView rvLeadsAndOpportunities_FCD;
    @ViewById
    protected TextView tvAttachments_FCD;
    @ViewById
    protected FrameLayout btnHistory;
    @ViewById
    protected ImageView ivIconArrow;
    @ViewById
    protected RecyclerView rvHistory;
    //endregion

    @Bean
    protected HistoryAdapter historyAdapter;
    @Bean
    protected ContactsAdapter contactsAdapter;
    @Bean
    protected OpportunityAndLeadsAdapter opportunityAndLeadsAdapter;
    @Bean
    protected CompaniesRepository companiesRepository;
    @Bean
    protected HistoryAnimationHelper animationHelper;

    @AfterInject
    @Override
    public void initPresenter() {
        new CompanyDetailsPresenter(this, companiesRepository, companyID);
    }

    @AfterViews
    protected void initUI() {
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvHistory.setAdapter(historyAdapter);

        rvContacts_FCD.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvContacts_FCD.setAdapter(contactsAdapter);

        rvLeadsAndOpportunities_FCD.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvLeadsAndOpportunities_FCD.setAdapter(opportunityAndLeadsAdapter);

        RxView.clicks(btnHistory)
                .throttleFirst(Constants.DELAY_CLICK, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> presenter.changeNotesVisibility());

        animationHelper.init(ivIconArrow, rvHistory);

        presenter.subscribe();
    }

    @Override
    protected void onRetry() {
        presenter.subscribe();
    }

    @Override
    protected void onRefreshData() {
        presenter.refresh();
    }
    //region Set data

    @Override
    public void showProgress(Constants.ProgressType type) {
        showProgressBar(type);
    }

    @Override
    public void showHistory(boolean enable) {
        if (enable && rvHistory.getVisibility() == View.GONE) {
            animationHelper.forward(nsvContent_FCD.getHeight());
        }
        if (!enable && rvHistory.getVisibility() == View.VISIBLE)
            animationHelper.backward(rvHistory.getHeight());
    }

    @Override
    public void displayErrorToast(String msg) {
        showErrorToast(msg);
    }

    @Override
    public void displayErrorState(String msg, ErrorViewHelper.ErrorType errorType) {
        showErrorState(msg, errorType);
    }

    @Override
    public void displayCompanyImage(String base64Image) {
        ImageHelper.getBitmapFromBase64(base64Image, new CropCircleTransformation())
                .subscribe(ivCompanyAvatar_FCD::setImageBitmap);
    }

    @Override
    public void displayCompanyName(String companyName) {
        tvCompanyName_FCD.setText(companyName);
    }

    @Override
    public void displayCompanyUrl(String companyUrl) {
        tvCompanyWebsite_FCD.setMovementMethod(LinkMovementMethod.getInstance());
        tvCompanyWebsite_FCD.setText(Html.fromHtml(companyUrl));
    }

    @Override
    public void enableFacebookButton(String url) {
        ivCompanyFb_FCD.setVisibility(View.VISIBLE);
        RxView.clicks(ivCompanyFb_FCD)
                .throttleFirst(Constants.DELAY_CLICK, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getActivity().startActivity(intent);
                });
    }

    @Override
    public void enableLinkedInButton(String url) {
        ivCompanyLinkedIn_FCD.setVisibility(View.VISIBLE);
        RxView.clicks(ivCompanyLinkedIn_FCD)
                .throttleFirst(Constants.DELAY_CLICK, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=" + url));
                    getActivity().startActivity(intent);
                });
    }

    @Override
    public void enableSkypeButton(String url) {
        ivCompanySkype_FCD.setVisibility(View.VISIBLE);
        RxView.clicks(ivCompanySkype_FCD)
                .throttleFirst(Constants.DELAY_CLICK, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    try {
                        Intent sky = new Intent(Intent.ACTION_VIEW);
                        sky.setData(Uri.parse("skype:" + url));
                        getActivity().startActivity(sky);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "Skype not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void displayEmail(String email) {
        etEmail_FCD.setText(email);
    }

    @Override
    public void displayAssignedTo(String assignedTo) {
        etAssignedTo_FCD.setText(assignedTo);
    }

    @Override
    public void displayLinkedIn(String linkedIn) {
        etLinkedIn_FCD.setText(linkedIn);
    }

    @Override
    public void displayFacebook(String facebook) {
        etFacebook_FCD.setText(facebook);
    }

    @Override
    public void displayPhone(String phone) {
        etPhone_FCD.setText(phone);
    }

    @Override
    public void displayMobile(String mobile) {
        etMobile_FCD.setText(mobile);
    }

    @Override
    public void displayBillingStreet(String billingStreet) {
        etBillingStreet_FCD.setText(billingStreet);
    }

    @Override
    public void displayBillingCity(String billingCity) {
        etBillingCity_FCD.setText(billingCity);
    }

    @Override
    public void displayBillingState(String billingState) {
        etBillingState_FCD.setText(billingState);
    }

    @Override
    public void displayBillingZip(String billingZip) {
        etBillingZip_FCD.setText(billingZip);
    }

    @Override
    public void displayBillingCountry(String billingCountry) {
        etBillingCountry_FCD.setText(billingCountry);
    }

    @Override
    public void displayShippingFullName(String shippingFullName) {
        etShippingFullname_FCD.setText(shippingFullName);
    }

    @Override
    public void displayShippingStreet(String shippingStreet) {
        etShippingStreet_FCD.setText(shippingStreet);
    }

    @Override
    public void displayShippingCity(String shippingCity) {
        etShippingCity_FCD.setText(shippingCity);
    }

    @Override
    public void displayShippingState(String shippingState) {
        etShippingState_FCD.setText(shippingState);
    }

    @Override
    public void displayShippingZip(String shippingZip) {
        etShippingZip_FCD.setText(shippingZip);
    }

    @Override
    public void displayShippingCountry(String shippingCountry) {
        etShippingCountry_FCD.setText(shippingCountry);
    }

    @Override
    public void displaySalesReference(String salesReference) {
        etReference_FCD.setText(salesReference);
    }

    @Override
    public void displaySalesIsCustomer(boolean isCustomer) {
        cbIsCustomer_FCD.setChecked(isCustomer);
    }

    @Override
    public void displaySalesIsSupplier(boolean isSupplier) {
        cbIsSupplier_FCD.setChecked(isSupplier);
    }

    @Override
    public void displaySalesTeam(String salesTeam) {
        etSalesTeam_FCD.setText(salesTeam);
    }

    @Override
    public void displaySalesPerson(String salesPerson) {
        etSalesPerson_FCD.setText(salesPerson);
    }

    @Override
    public void displaySalesImplementedBy(String salesImplementedBy) {
        etImplementedBy_FCD.setText(salesImplementedBy);
    }

    @Override
    public void displaySalesLanguage(String salesLanguage) {
        etLanguage_FCD.setText(salesLanguage);
    }

    @Override
    public void displayContacts(ArrayList<ContactDH> contactDHs) {
        contactsAdapter.setListDH(contactDHs);
    }

    @Override
    public void displayLeadAndOpportunity(ArrayList<OpportunityAndLeadDH> opportunityAndLeadDHs) {
        opportunityAndLeadsAdapter.setListDH(opportunityAndLeadDHs);
    }

    @Override
    public void displayAttachments(String attachments) {
        tvAttachments_FCD.setMovementMethod(LinkMovementMethod.getInstance());
        tvAttachments_FCD.setText(Html.fromHtml(attachments));
    }

    @Override
    public void displayHistory(ArrayList<HistoryDH> historyDHs) {
        historyAdapter.setListDH(historyDHs);
    }
    //endregion

    @Override
    public boolean optionsMenuForDetail() {
        return true;
    }

    @Override
    public void setPresenter(CompanyDetailsContract.CompanyDetailsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onDestroyView() {
        animationHelper.cancel();
        presenter.unsubscribe();
        super.onDestroyView();
    }
}
