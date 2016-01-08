package purchase;

import common.contexts.UserContext;
import common.controllers.ControllerDependency;
import common.controllers.SunrisePageData;
import common.models.ProductDataConfig;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.SetShippingMethod;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.shippingmethods.ShippingMethod;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CheckoutShippingController extends CartController {


    private final ShippingMethods shippingMethods;
    private final ProductDataConfig productDataConfig;

    @Inject
    public CheckoutShippingController(final ControllerDependency controllerDependency, final ShippingMethods shippingMethods, final ProductDataConfig productDataConfig) {
        super(controllerDependency);
        this.shippingMethods = shippingMethods;
        this.productDataConfig = productDataConfig;
    }

    public F.Promise<Result> show(final String languageTag) {
        final UserContext userContext = userContext(languageTag);
        final F.Promise<Cart> cartPromise = getOrCreateCart(userContext, session());
        return cartPromise.map(cart -> {
            final CheckoutShippingPageContent content = new CheckoutShippingPageContent(cart, i18nResolver(), userContext, shippingMethods, productDataConfig, reverseRouter());
            final SunrisePageData pageData = pageData(userContext, content, ctx());
            return ok(templateService().renderToHtml("checkout-shipping", pageData, userContext.locales()));
        });
    }

    @AddCSRFToken
    @RequireCSRFCheck
    public F.Promise<Result> process(final String languageTag) {
        final UserContext userContext = userContext(languageTag);
        return getOrCreateCart(userContext, session()).flatMap(cart -> {
            final CheckoutShippingFormData checkoutShippingFormData = extractBean(request(), CheckoutShippingFormData.class);
            final Form<CheckoutShippingFormData> filledForm = obtainFilledForm();
            final CheckoutShippingPageContent content = new CheckoutShippingPageContent(checkoutShippingFormData, cart, i18nResolver(), userContext, shippingMethods, productDataConfig, reverseRouter());
            if (filledForm.hasErrors()) {
                return F.Promise.pure(badRequest(userContext, filledForm, content));
            } else {
                return updateCart(sphere(), cart, checkoutShippingFormData, content)
                        .map(updatedCart -> redirect(reverseRouter().showCheckoutPaymentForm(languageTag)));
            }
        });
    }

    private F.Promise<Cart> updateCart(final PlayJavaSphereClient sphere, final Cart cart, final CheckoutShippingFormData checkoutShippingFormData, final CheckoutShippingPageContent content) {
        final String shippingMethodId = checkoutShippingFormData.getShippingMethodId();
        final SetShippingMethod setShippingMethod = SetShippingMethod.of(Reference.of(ShippingMethod.referenceTypeId(), shippingMethodId));
        return sphere.execute(CartUpdateCommand.of(cart, setShippingMethod));
    }

    private Result badRequest(final UserContext userContext, final Form<CheckoutShippingFormData> filledForm, final CheckoutShippingPageContent content) {
        Logger.info("cart not valid");
        content.getShippingForm().setErrors(new ErrorsBean(filledForm));
        final SunrisePageData pageData = pageData(userContext, content, ctx());
        return badRequest(templateService().renderToHtml("checkout-shipping", pageData, userContext.locales()));
    }

    private Form<CheckoutShippingFormData> obtainFilledForm() {
        return Form.form(CheckoutShippingFormData.class, CheckoutShippingFormData.Validation.class).bindFromRequest(request());
    }

    private static <T> T extractBean(final Http.Request request, final Class<T> clazz) {
        return DynamicForm.form(clazz, null).bindFromRequest(request).get();
    }
}