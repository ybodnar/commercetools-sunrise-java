package com.commercetools.sunrise.models.products;

import com.commercetools.sunrise.core.hooks.HookRunner;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.products.queries.ProductProjectionQueryModel;
import io.sphere.sdk.products.search.PriceSelection;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.QueryPredicate;
import play.i18n.Lang;
import play.i18n.Langs;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Optional;

public class DefaultProductFetcher extends AbstractProductFetcher {

    private final Locale locale;
    private final Langs langs;
    private final PriceSelection priceSelection;

    @Inject
    protected DefaultProductFetcher(final SphereClient sphereClient, final HookRunner hookRunner, final Locale locale,
                                    final Langs langs, final PriceSelection priceSelection) {
        super(sphereClient, hookRunner);
        this.locale = locale;
        this.langs = langs;
        this.priceSelection = priceSelection;
    }

    @Override
    public Optional<ProductProjectionQuery> defaultRequest(final String productIdentifier) {
        return buildSlugPredicate(productIdentifier)
                .map(slugPredicate -> ProductProjectionQuery.ofCurrent()
                        .withPriceSelection(priceSelection)
                        .withPredicates(slugPredicate));
    }

    @Override
    protected Optional<ProductProjection> selectProduct(final PagedQueryResult<ProductProjection> result,
                                                         final String slug, final String sku) {
        if (result.getTotal() > 1) {
            return result.getResults().stream()
                    .filter(product -> productMatchesSlugInUsersLanguage(product, slug))
                    .findAny()
                    .map(Optional::of)
                    .orElseGet(() -> super.selectResource(result));
        } else {
            return super.selectResource(result);
        }
    }

    @Override
    protected ProductVariant selectVariant(final ProductProjection product, final String sku) {
        return product.findVariantBySku(sku).orElseGet(product::getMasterVariant);
    }

    private boolean productMatchesSlugInUsersLanguage(final ProductProjection product, final String slug) {
        return product.getSlug().find(locale)
                .map(slugInUsersLang -> slugInUsersLang.equals(slug))
                .orElse(false);
    }

    private Optional<QueryPredicate<ProductProjection>> buildSlugPredicate(final String slug) {
        return langs.availables().parallelStream()
                .map(lang -> Optional.of(buildSingleSlugPredicate(slug, lang)))
                .reduce(Optional.empty(), (opt1, opt2) ->
                        opt1.map(model1 -> Optional.of(opt2.map(model1::or).orElse(model1))).orElse(opt2));
    }

    private QueryPredicate<ProductProjection> buildSingleSlugPredicate(final String slug, final Lang lang) {
        return ProductProjectionQueryModel.of().slug().lang(lang.toLocale()).is(slug);
    }
}