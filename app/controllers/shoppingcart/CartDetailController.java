package controllers.shoppingcart;

import com.commercetools.sunrise.framework.controllers.cache.NoCache;
import com.commercetools.sunrise.framework.hooks.RegisteredComponents;
import com.commercetools.sunrise.framework.template.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.framework.template.engine.TemplateRenderer;
import com.commercetools.sunrise.sessions.cart.CartOperationsControllerComponentSupplier;
import com.commercetools.sunrise.shoppingcart.CartFinder;
import com.commercetools.sunrise.shoppingcart.cart.cartdetail.SunriseCartDetailController;
import com.commercetools.sunrise.shoppingcart.cart.cartdetail.viewmodels.CartDetailPageContentFactory;
import controllers.PageHeaderControllerComponentSupplier;

import javax.inject.Inject;

@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        PageHeaderControllerComponentSupplier.class,
        CartOperationsControllerComponentSupplier.class
})
public final class CartDetailController extends SunriseCartDetailController {

    @Inject
    public CartDetailController(final TemplateRenderer templateRenderer,
                                final CartFinder cartFinder,
                                final CartDetailPageContentFactory cartDetailPageContentFactory) {
        super(templateRenderer, cartFinder, cartDetailPageContentFactory);
    }

    @Override
    public String getTemplateName() {
        return "cart";
    }
}
