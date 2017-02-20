package controllers.myaccount;

import com.commercetools.sunrise.framework.controllers.cache.NoCache;
import com.commercetools.sunrise.framework.hooks.RegisteredComponents;
import com.commercetools.sunrise.framework.reverserouters.myaccount.AddressBookReverseRouter;
import com.commercetools.sunrise.framework.reverserouters.myaccount.AuthenticationReverseRouter;
import com.commercetools.sunrise.framework.template.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.framework.template.engine.TemplateRenderer;
import com.commercetools.sunrise.myaccount.CustomerFinder;
import com.commercetools.sunrise.myaccount.addressbook.AddressFinder;
import com.commercetools.sunrise.myaccount.addressbook.DefaultAddressFormData;
import com.commercetools.sunrise.myaccount.addressbook.changeaddress.ChangeAddressControllerAction;
import com.commercetools.sunrise.myaccount.addressbook.changeaddress.SunriseChangeAddressController;
import com.commercetools.sunrise.myaccount.addressbook.changeaddress.viewmodels.ChangeAddressPageContentFactory;
import com.commercetools.sunrise.sessions.customer.CustomerOperationsControllerComponentSupplier;
import controllers.PageHeaderControllerComponentSupplier;
import io.sphere.sdk.customers.Customer;
import play.data.FormFactory;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        PageHeaderControllerComponentSupplier.class,
        CustomerOperationsControllerComponentSupplier.class
})
public final class ChangeAddressController extends SunriseChangeAddressController<DefaultAddressFormData> {

    private final AuthenticationReverseRouter authenticationReverseRouter;
    private final AddressBookReverseRouter addressBookReverseRouter;

    @Inject
    public ChangeAddressController(final TemplateRenderer templateRenderer,
                                   final FormFactory formFactory,
                                   final CustomerFinder customerFinder,
                                   final AddressFinder addressFinder,
                                   final ChangeAddressControllerAction changeAddressControllerAction,
                                   final ChangeAddressPageContentFactory changeAddressPageContentFactory,
                                   final AuthenticationReverseRouter authenticationReverseRouter,
                                   final AddressBookReverseRouter addressBookReverseRouter) {
        super(templateRenderer, formFactory, customerFinder, addressFinder, changeAddressControllerAction, changeAddressPageContentFactory);
        this.authenticationReverseRouter = authenticationReverseRouter;
        this.addressBookReverseRouter = addressBookReverseRouter;
    }

    @Override
    public String getTemplateName() {
        return "my-account-edit-address";
    }

    @Override
    public Class<DefaultAddressFormData> getFormDataClass() {
        return DefaultAddressFormData.class;
    }

    @Override
    public CompletionStage<Result> handleNotFoundCustomer() {
        return redirectTo(authenticationReverseRouter.logInPageCall());
    }

    @Override
    public CompletionStage<Result> handleNotFoundAddress() {
        return redirectTo(addressBookReverseRouter.addressBookDetailPageCall());
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final Customer updatedCustomer, final DefaultAddressFormData formData) {
        return redirectTo(addressBookReverseRouter.addressBookDetailPageCall());
    }
}
