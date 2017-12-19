package com.commercetools.sunrise.core.viewmodels.content;

import com.commercetools.sunrise.core.viewmodels.FormViewModelFactory;
import play.data.Form;

public abstract class FormPageContentFactory<M extends PageContent, I, F> extends FormViewModelFactory<M, I, F> {

    @Override
    protected void initialize(final M viewModel, final I input, final Form<? extends F> form) {
        fillTitle(viewModel, input, form);
        fillMessages(viewModel, input, form);
    }

    @Deprecated
    protected void fillTitle(final M viewModel, final I input, final Form<? extends F> form) {

    }

    protected void fillMessages(final M viewModel, final I input, final Form<? extends F> form) {
        viewModel.addMessages(extractMessages());
    }
}
