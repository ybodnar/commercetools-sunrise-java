package com.commercetools.sunrise.myaccount.changepassword;

import com.commercetools.sunrise.core.FormAction;
import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultChangePasswordFormAction.class)
public interface ChangePasswordFormAction extends FormAction<ChangePasswordFormData> {

}
