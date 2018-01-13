package com.commercetools.sunrise.models.customers;

import com.commercetools.sunrise.core.hooks.HookRunner;
import com.commercetools.sunrise.core.injection.RequestScoped;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.queries.CustomerQuery;
import io.sphere.sdk.customers.queries.CustomerQueryBuilder;

import javax.inject.Inject;
import java.util.Optional;

@RequestScoped
public class DefaultMyCustomerFetcher extends AbstractCustomerFetcher {

    private final MyCustomerInSession myCustomerInSession;

    @Inject
    protected DefaultMyCustomerFetcher(final SphereClient sphereClient, final HookRunner hookRunner,
                                       final MyCustomerInSession myCustomerInSession) {
        super(sphereClient, hookRunner);
        this.myCustomerInSession = myCustomerInSession;
    }

    protected final MyCustomerInSession getMyCustomerInSession() {
        return myCustomerInSession;
    }

    @Override
    public Optional<CustomerQuery> defaultRequest() {
        return myCustomerInSession.findId()
                .map(customerId -> CustomerQueryBuilder.of()
                        .plusPredicates(customer -> customer.id().is(customerId))
                        .build());
    }
}
