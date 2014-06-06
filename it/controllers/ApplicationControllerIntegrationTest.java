package controllers;

import io.sphere.sdk.categories.Categories;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.client.PagedQueryResult;
import org.junit.Test;
import testutils.WithPlayJavaClient;

import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

public class ApplicationControllerIntegrationTest extends WithPlayJavaClient {
    @Test
    public void itFindsSomeCategories() throws Exception {
        final PagedQueryResult<Category> queryResult = client.execute(Categories.query()).get(1000);
        final int count = queryResult.getCount();
        assertThat(count).isGreaterThan(3);
        //this is a project specific assertion as example
        assertThat(queryResult.getResults().get(0).getName().get(Locale.ENGLISH).get()).isEqualTo("Tank tops");
    }
}
