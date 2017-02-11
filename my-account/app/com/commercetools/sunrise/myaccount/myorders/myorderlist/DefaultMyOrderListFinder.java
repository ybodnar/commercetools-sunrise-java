package com.commercetools.sunrise.myaccount.myorders.myorderlist;

import com.commercetools.sunrise.hooks.RequestHookContext;
import com.commercetools.sunrise.hooks.requests.OrderQueryHook;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.queries.OrderQuery;
import io.sphere.sdk.queries.PagedQueryResult;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class DefaultMyOrderListFinder implements MyOrderListFinder {

    private final SphereClient sphereClient;
    private final RequestHookContext hookContext;

    @Inject
    protected DefaultMyOrderListFinder(final SphereClient sphereClient, final RequestHookContext hookContext) {
        this.sphereClient = sphereClient;
        this.hookContext = hookContext;
    }

    @Override
    public CompletionStage<PagedQueryResult<Order>> findOrders(final Customer customer) {
        final OrderQuery baseQuery = buildRequest(customer);
        final OrderQuery query = runHookOnOrderQuery(baseQuery);
        return sphereClient.execute(query);
    }

    protected OrderQuery buildRequest(final Customer customer) {
        return OrderQuery.of()
                    .byCustomerId(customer.getId())
                    .withSort(order -> order.createdAt().sort().desc());
    }

    private OrderQuery runHookOnOrderQuery(final OrderQuery baseQuery) {
        return OrderQueryHook.runHook(hookContext, baseQuery);
    }
}
