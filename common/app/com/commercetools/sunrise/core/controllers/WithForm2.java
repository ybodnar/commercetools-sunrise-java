package com.commercetools.sunrise.core.controllers;

import play.data.Form;
import play.data.FormFactory;

public interface WithForm2<F> {

    default Form<? extends F> bindForm() {
        return createForm().bindFromRequest();
    }

    default Form<? extends F> createForm() {
        return getFormFactory().form(getFormDataClass());
    }

    Class<? extends F> getFormDataClass();

    FormFactory getFormFactory();
}