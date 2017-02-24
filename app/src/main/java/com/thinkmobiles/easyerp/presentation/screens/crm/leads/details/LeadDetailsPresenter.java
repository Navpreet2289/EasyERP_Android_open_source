package com.thinkmobiles.easyerp.presentation.screens.crm.leads.details;


import android.text.TextUtils;

import com.thinkmobiles.easyerp.data.model.crm.leads.detail.AttachmentItem;
import com.thinkmobiles.easyerp.data.model.crm.leads.detail.ResponseGetLeadDetails;
import com.thinkmobiles.easyerp.presentation.base.rules.content.ContentPresenterHelper;
import com.thinkmobiles.easyerp.presentation.base.rules.content.ContentView;
import com.thinkmobiles.easyerp.presentation.holders.data.crm.AttachmentDH;
import com.thinkmobiles.easyerp.presentation.holders.data.crm.HistoryDH;
import com.thinkmobiles.easyerp.presentation.managers.DateManager;
import com.thinkmobiles.easyerp.presentation.utils.Constants;
import com.thinkmobiles.easyerp.presentation.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;

public class LeadDetailsPresenter extends ContentPresenterHelper implements LeadDetailsContract.LeadDetailsPresenter {

    private LeadDetailsContract.LeadDetailsView view;
    private LeadDetailsContract.LeadDetailsModel model;
    private String leadId;

    private ResponseGetLeadDetails currentLead;
    private boolean isVisibleHistory;

    public LeadDetailsPresenter(LeadDetailsContract.LeadDetailsView view, LeadDetailsContract.LeadDetailsModel model, String leadId) {
        this.view = view;
        this.model = model;
        this.leadId = leadId;
        view.setPresenter(this);
    }

    @Override
    public void changeNotesVisibility() {
        isVisibleHistory = !isVisibleHistory;
        view.showHistory(isVisibleHistory);
    }

    @Override
    public void refresh() {
        compositeSubscription.add(model.getLeadDetails(leadId)
                .subscribe(responseGetLeadDetails -> {
                    currentLead = responseGetLeadDetails;
                    setData(currentLead);
                    view.showProgress(Constants.ProgressType.NONE);
                }, t -> error(t)));
    }

    @Override
    public void startAttachment(int pos) {
        String url = String.format("%sdownload/%s", Constants.BASE_URL, currentLead.attachments.get(pos).shortPath);
        view.startUrlIntent(url);
    }

    private void setData(ResponseGetLeadDetails response) {
        displayBaseInfo(response);
        displayContacts(response);
        displayCompany(response);
        displayTags(response);
        displayAttachments(response);
        displayHistory(response);
    }

    @Override
    protected ContentView getView() {
        return view;
    }

    @Override
    protected boolean hasContent() {
        return currentLead != null;
    }

    @Override
    protected void retainInstance() {
        setData(currentLead);
    }

    private void displayBaseInfo(ResponseGetLeadDetails response) {
        if(response.workflow != null && !TextUtils.isEmpty(response.workflow.name)) view.setCurrentStatus(response.workflow.name);
        if(!TextUtils.isEmpty(response.name)) view.setLeadName(response.name);
        if(!TextUtils.isEmpty(response.expectedClosing)) view.setCloseDate(DateManager.convert(response.expectedClosing).toString());
        if(response.salesPerson != null && !TextUtils.isEmpty(response.salesPerson.fullName))view.setAssignedTo(response.salesPerson.fullName);
        if(!TextUtils.isEmpty(response.priority)) view.setPriority(response.priority);
        if(!TextUtils.isEmpty(response.source)) view.setSource(response.source);
    }

    private void displayContacts(ResponseGetLeadDetails response) {
        boolean contactInfoAvailable = false;
        if(response.contactName != null) {
            if(!TextUtils.isEmpty(response.contactName.first) && !TextUtils.isEmpty(response.contactName.last)) {
                view.setPersonName(StringUtil.getFullName(response.contactName.first, response.contactName.last));
                contactInfoAvailable = true;
            }
            if(!TextUtils.isEmpty(response.contactName.first)) {
                view.setFirstName(response.contactName.first);
                contactInfoAvailable = true;
            }
            if(!TextUtils.isEmpty(response.contactName.last)) {
                view.setLastName(response.contactName.last);
                contactInfoAvailable = true;
            }
        }
        if(!TextUtils.isEmpty(response.jobPosition)) {
            view.setJobPosition(response.jobPosition);
            contactInfoAvailable = true;
        }
        if(!TextUtils.isEmpty(response.dateBirth)) {
            view.setDob(new DateManager.DateConverter(response.dateBirth)
                    .setDstPattern(DateManager.PATTERN_SIMPLE_DATE)
                    .toString());
            contactInfoAvailable = true;
        }
        if(!TextUtils.isEmpty(response.email)) {
            view.setEmail(response.email);
            contactInfoAvailable = true;
        }
        if(response.phones != null) {
            if(!TextUtils.isEmpty(response.phones.phone)) {
                view.setPhone(response.phones.phone);
                contactInfoAvailable = true;
            }
            else if(!TextUtils.isEmpty(response.phones.mobile)) {
                view.setPhone(response.phones.mobile);
                contactInfoAvailable = true;
            }
            else if(!TextUtils.isEmpty(response.phones.fax)) {
                view.setPhone(response.phones.fax);
                contactInfoAvailable = true;
            }
        }
        if(!TextUtils.isEmpty(response.skype)) {
            view.setSkype(response.skype);
            contactInfoAvailable = true;
        }
        if(response.social != null) {
            if(!TextUtils.isEmpty(response.social.linkedIn)) {
                view.setLinkedIn(response.social.linkedIn);
                contactInfoAvailable = true;
            }
            if(!TextUtils.isEmpty(response.social.facebook)){
                view.setTvFacebook(response.social.facebook);
                contactInfoAvailable = true;
            }
        }
        view.showContacts(contactInfoAvailable);
    }

    private void displayCompany(ResponseGetLeadDetails response) {
        boolean isCompanyInfoAvailable = false;
        if(!TextUtils.isEmpty(response.tempCompanyField)) {
            view.setCompanyName(response.tempCompanyField);
            isCompanyInfoAvailable = true;
        }
        if (response.company != null) {
            if(response.company.address != null) {
                if(!TextUtils.isEmpty(response.company.address.street)) {
                    view.setCompanyStreet(response.company.address.street);
                    isCompanyInfoAvailable = true;
                }
                if(!TextUtils.isEmpty(response.company.address.city)) {
                    view.setCompanyCity(response.company.address.city);
                    isCompanyInfoAvailable = true;
                }
                if(!TextUtils.isEmpty(response.company.address.state)) {
                    view.setCompanyState(response.company.address.state);
                    isCompanyInfoAvailable = true;
                }
                if(!TextUtils.isEmpty(response.company.address.zip)) {
                    view.setCompanyZipcode(response.company.address.zip);
                    isCompanyInfoAvailable = true;
                }
                if(!TextUtils.isEmpty(response.company.address.country)) {
                    view.setCompanyCountry(response.company.address.country);
                    isCompanyInfoAvailable = true;
                }
            }
        }
        view.showCompany(isCompanyInfoAvailable);
    }

    private void displayTags(ResponseGetLeadDetails response) {
        if(response.tags != null && !response.tags.isEmpty()) {
            view.setTags(response.tags);
        }
    }

    private void displayAttachments(ResponseGetLeadDetails response) {
        if (response.attachments != null && !response.attachments.isEmpty()) {
            ArrayList<AttachmentDH> result = new ArrayList<>();
            for(AttachmentItem item : response.attachments) result.add(new AttachmentDH(item));
            view.displayAttachments(result);
        } else
            view.showAttachments(false);
    }

    private void displayHistory(ResponseGetLeadDetails response) {
        Collections.reverse(response.notes);
        view.setHistory(HistoryDH.convert(response.notes));
        view.showHistory(isVisibleHistory);
    }
}
