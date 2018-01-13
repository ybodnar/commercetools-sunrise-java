package com.commercetools.sunrise.myaccount.addressbook;

import io.sphere.sdk.customers.commands.updateactions.RemoveAddress;
import io.sphere.sdk.models.Base;
import play.data.validation.Constraints;

public class DefaultRemoveAddressFormData extends Base implements RemoveAddressFormData {

    @Constraints.Required
    private String addressId;

    @Override
    public RemoveAddress removeAddress() {
        return RemoveAddress.of(addressId);
    }
}