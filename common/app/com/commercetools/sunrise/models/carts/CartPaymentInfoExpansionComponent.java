package com.commercetools.sunrise.models.carts;

import com.commercetools.sunrise.core.components.ControllerComponent;
import com.commercetools.sunrise.core.hooks.ctprequests.CartQueryHook;
import com.commercetools.sunrise.core.hooks.ctprequests.CartUpdateCommandHook;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.expansion.CartExpansionModel;
import io.sphere.sdk.carts.expansion.PaymentInfoExpansionModel;
import io.sphere.sdk.carts.queries.CartQuery;

/**
 * This controller component expands the carts payment info with the payments.
 *
 * @see Cart#getPaymentInfo()
 * @see CartExpansionModel#paymentInfo()
 * @see PaymentInfoExpansionModel#payments()
 */
public final class CartPaymentInfoExpansionComponent implements ControllerComponent, CartQueryHook, CartUpdateCommandHook {

    @Override
    public CartQuery onCartQuery(final CartQuery cartQuery) {
        return cartQuery.plusExpansionPaths(m -> m.paymentInfo().payments());
    }

    @Override
    public CartUpdateCommand onCartUpdateCommand(final CartUpdateCommand cartUpdateCommand){
        return cartUpdateCommand.plusExpansionPaths(m -> m.paymentInfo().payments());
    }
}
