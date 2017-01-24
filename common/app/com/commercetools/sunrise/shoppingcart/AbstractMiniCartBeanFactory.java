package com.commercetools.sunrise.shoppingcart;

import com.commercetools.sunrise.common.models.ViewModelFactory;
import com.commercetools.sunrise.common.utils.PriceFormatter;
import io.sphere.sdk.carts.CartLike;
import io.sphere.sdk.carts.LineItem;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.PriceUtils;

import javax.annotation.Nullable;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.util.stream.Collectors;

import static com.commercetools.sunrise.common.utils.CartPriceUtils.calculateTotalPrice;

public abstract class AbstractMiniCartBeanFactory<T extends MiniCartBean, C extends CartLike<?>> extends ViewModelFactory<T, AbstractMiniCartBeanFactory.Data<C>> {

    private final CurrencyUnit currency;
    private final PriceFormatter priceFormatter;

    protected AbstractMiniCartBeanFactory(final CurrencyUnit currency, final PriceFormatter priceFormatter) {
        this.currency = currency;
        this.priceFormatter = priceFormatter;
    }

    protected abstract LineItemBean createLineItem(final LineItem lineItem);

    @Override
    protected void initialize(final T bean, final Data<C> data) {
        fillTotalPrice(bean, data);
        fillTotalItems(bean, data);
        fillLineItems(bean, data);
    }

    protected void fillTotalItems(final T bean, final Data<C> data) {
        final long totalItems;
        if (data.cartLike != null) {
            totalItems = data.cartLike.getLineItems().stream()
                    .mapToLong(LineItem::getQuantity)
                    .sum();
        } else {
            totalItems = 0;
        }
        bean.setTotalItems(totalItems);
    }

    protected void fillLineItems(final T bean, final Data<C> data) {
        if (data.cartLike != null) {
            bean.setLineItems(createLineItemList(data.cartLike));
        }
    }

    protected void fillTotalPrice(final T bean, final Data<C> data) {
        final MonetaryAmount totalPrice;
        if (data.cartLike != null) {
            totalPrice = calculateTotalPrice(data.cartLike);
        } else {
            totalPrice = zeroAmount(currency);
        }
        bean.setTotalPrice(priceFormatter.format(totalPrice));
    }

    private LineItemListBean createLineItemList(final CartLike<?> cartLike) {
        final LineItemListBean lineItemListBean = new LineItemListBean();
        lineItemListBean.setList(cartLike.getLineItems().stream()
                .map(this::createLineItem)
                .collect(Collectors.toList()));
        return lineItemListBean;
    }

    private MonetaryAmount zeroAmount(final CurrencyUnit currency) {
        return PriceUtils.zeroAmount(currency);
    }

    protected final static class Data<C> extends Base {

        @Nullable
        public final C cartLike;

        public Data(@Nullable final C cartLike) {
            this.cartLike = cartLike;
        }
    }
}