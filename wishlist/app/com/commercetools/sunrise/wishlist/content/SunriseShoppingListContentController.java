package com.commercetools.sunrise.wishlist.content;

import com.commercetools.sunrise.framework.controllers.SunriseContentController;
import com.commercetools.sunrise.framework.controllers.WithQueryFlow;
import com.commercetools.sunrise.framework.hooks.EnableHooks;
import com.commercetools.sunrise.framework.reverserouters.SunriseRoute;
import com.commercetools.sunrise.framework.reverserouters.wishlist.WishlistReverseRouter;
import com.commercetools.sunrise.framework.template.engine.ContentRenderer;
import com.commercetools.sunrise.framework.viewmodels.content.PageContent;
import com.commercetools.sunrise.wishlist.ShoppingListFinder;
import com.commercetools.sunrise.wishlist.ShoppingListTypeIdentifier;
import com.commercetools.sunrise.wishlist.WithRequiredShoppingList;
import com.commercetools.sunrise.wishlist.content.viewmodels.ShoppingListPageContentFactory;
import io.sphere.sdk.shoppinglists.ShoppingList;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * This controller is used to view the current shoppinglist.
 */
public abstract class SunriseShoppingListContentController extends SunriseContentController implements WithQueryFlow<ShoppingList>, WithRequiredShoppingList,ShoppingListTypeIdentifier {

    private final ShoppingListFinder shoppingListFinder;
    private final ShoppingListPageContentFactory shoppingListPageContentFactory;

    @Inject
    protected SunriseShoppingListContentController(final ContentRenderer contentRenderer,
                                                   final ShoppingListPageContentFactory shoppingListPageContentFactory,
                                                   final ShoppingListFinder shoppingListFinder) {
        super(contentRenderer);
        this.shoppingListPageContentFactory = shoppingListPageContentFactory;
        this.shoppingListFinder = shoppingListFinder;
    }

    @Override
    public final ShoppingListFinder getShoppingListFinder() {
        return shoppingListFinder;
    }

    @EnableHooks
    @SunriseRoute(WishlistReverseRouter.WISHLIST_PAGE)
    public CompletionStage<Result> show(final String languageTag) {
        return requireShoppingList(this::showPage, getShoppingListType());
    }

    @Override
    public PageContent createPageContent(final ShoppingList wishlist) {
        return shoppingListPageContentFactory.create(wishlist);
    }

    @Override
    public CompletionStage<Result> handleNotFoundShoppingList() {
        return okResultWithPageContent(shoppingListPageContentFactory.create(null));
    }
}
