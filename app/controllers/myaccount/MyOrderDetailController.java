package controllers.myaccount;

import com.commercetools.sunrise.framework.controllers.cache.NoCache;
import com.commercetools.sunrise.framework.hooks.RegisteredComponents;
import com.commercetools.sunrise.framework.reverserouters.myaccount.AuthenticationReverseRouter;
import com.commercetools.sunrise.framework.reverserouters.myaccount.MyOrdersReverseRouter;
import com.commercetools.sunrise.framework.template.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.framework.template.engine.TemplateRenderer;
import com.commercetools.sunrise.myaccount.CustomerFinder;
import com.commercetools.sunrise.myaccount.myorders.myorderdetail.MyOrderFinder;
import com.commercetools.sunrise.myaccount.myorders.myorderdetail.SunriseMyOrderDetailController;
import com.commercetools.sunrise.myaccount.myorders.myorderdetail.viewmodels.MyOrderDetailPageContentFactory;
import com.commercetools.sunrise.sessions.customer.CustomerOperationsControllerComponentSupplier;
import controllers.PageHeaderControllerComponentSupplier;
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
public final class MyOrderDetailController extends SunriseMyOrderDetailController {

    private final MyOrdersReverseRouter myOrdersReverseRouter;
    private final AuthenticationReverseRouter authenticationReverseRouter;

    @Inject
    public MyOrderDetailController(final TemplateRenderer templateRenderer,
                                   final FormFactory formFactory,
                                   final CustomerFinder customerFinder,
                                   final MyOrderFinder myOrderFinder,
                                   final MyOrderDetailPageContentFactory myOrderDetailPageContentFactory,
                                   final MyOrdersReverseRouter myOrdersReverseRouter,
                                   final AuthenticationReverseRouter authenticationReverseRouter) {
        super(templateRenderer, formFactory, customerFinder, myOrderFinder, myOrderDetailPageContentFactory);
        this.myOrdersReverseRouter = myOrdersReverseRouter;
        this.authenticationReverseRouter = authenticationReverseRouter;
    }

    @Override
    public String getTemplateName() {
        return "my-account-my-orders-order";
    }

    @Override
    public CompletionStage<Result> handleNotFoundCustomer() {
        return redirectTo(authenticationReverseRouter.logInPageCall());
    }

    @Override
    public CompletionStage<Result> handleNotFoundMyOrder() {
        return redirectTo(myOrdersReverseRouter.myOrderListPageCall());
    }
}
