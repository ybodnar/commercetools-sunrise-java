package com.commercetools.sunrise.myaccount.authentication.login;

import com.commercetools.sunrise.common.pages.PageContent;
import com.commercetools.sunrise.framework.controllers.SunriseTemplateFormController;
import com.commercetools.sunrise.framework.controllers.WithTemplateFormFlow;
import com.commercetools.sunrise.framework.hooks.RunRequestStartedHook;
import com.commercetools.sunrise.framework.reverserouters.SunriseRoute;
import com.commercetools.sunrise.framework.reverserouters.myaccount.AuthenticationReverseRouter;
import com.commercetools.sunrise.framework.template.engine.TemplateRenderer;
import com.commercetools.sunrise.myaccount.MyAccountController;
import com.commercetools.sunrise.myaccount.authentication.login.viewmodels.LogInPageContentFactory;
import io.sphere.sdk.client.ClientErrorException;
import io.sphere.sdk.customers.CustomerSignInResult;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

import static com.commercetools.sunrise.common.utils.SphereExceptionUtils.isCustomerInvalidCredentialsError;

public abstract class SunriseLogInController<F extends LogInFormData> extends SunriseTemplateFormController
        implements MyAccountController, WithTemplateFormFlow<F, Void, CustomerSignInResult> {

    private final LogInControllerAction logInControllerAction;
    private final LogInPageContentFactory logInPageContentFactory;

    protected SunriseLogInController(final TemplateRenderer templateRenderer, final FormFactory formFactory,
                                     final LogInControllerAction logInControllerAction,
                                     final LogInPageContentFactory logInPageContentFactory) {
        super(templateRenderer, formFactory);
        this.logInControllerAction = logInControllerAction;
        this.logInPageContentFactory = logInPageContentFactory;
    }

    @RunRequestStartedHook
    @SunriseRoute(AuthenticationReverseRouter.LOG_IN_PAGE)
    public CompletionStage<Result> show(final String languageTag) {
        return showFormPage(null);
    }

    @RunRequestStartedHook
    @SunriseRoute(AuthenticationReverseRouter.LOG_IN_PROCESS)
    public CompletionStage<Result> process(final String languageTag) {
        return processForm(null);
    }

    @Override
    public CompletionStage<CustomerSignInResult> executeAction(final Void input, final F formData) {
        return logInControllerAction.apply(formData);
    }

    @Override
    public CompletionStage<Result> handleClientErrorFailedAction(final Void input, final Form<F> form, final ClientErrorException clientErrorException) {
        if (isCustomerInvalidCredentialsError(clientErrorException)) {
            saveFormError(form, "Invalid credentials"); // TODO i18n
            return showFormPageWithErrors(input, form);
        } else {
            return WithTemplateFormFlow.super.handleClientErrorFailedAction(input, form, clientErrorException);
        }
    }

    @Override
    public abstract CompletionStage<Result> handleSuccessfulAction(final CustomerSignInResult result, final F formData);

    @Override
    public PageContent createPageContent(final Void input, final Form<F> form) {
        return logInPageContentFactory.create(form);
    }

    @Override
    public void preFillFormData(final Void input, final F formData) {
        // Do nothing
    }
}
