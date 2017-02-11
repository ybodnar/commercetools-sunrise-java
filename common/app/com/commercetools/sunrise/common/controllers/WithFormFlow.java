package com.commercetools.sunrise.common.controllers;

import io.sphere.sdk.client.ClientErrorException;
import org.apache.commons.beanutils.BeanUtils;
import play.data.Form;
import play.libs.concurrent.HttpExecution;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Html;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static io.sphere.sdk.utils.CompletableFutureUtils.exceptionallyCompletedFuture;
import static io.sphere.sdk.utils.CompletableFutureUtils.recoverWith;

/**
 * Approach to handle form data (Template Method Pattern).
 * @param <F> stereotype of the in a form wrapped class
 * @param <T> type of the context of the form, possibly a parameter object
 * @param <R> the type of the updated object if the form is valid
 */
public interface WithFormFlow<F, T, R> extends WithForm<F> {

    default CompletionStage<Result> showForm(final T context) {
        final Form<F> form = createNewFilledForm(context);
        return renderPage(form, context, null)
                .thenApplyAsync(Results::ok, HttpExecution.defaultContext());
    }

    default CompletionStage<Result> validateForm(final T context) {
        return bindForm().thenComposeAsync(form -> {
            if (!form.hasErrors()) {
                return handleValidForm(form, context);
            } else {
                return handleInvalidForm(form, context);
            }
        }, HttpExecution.defaultContext());
    }

    default CompletionStage<Result> handleInvalidForm(final Form<F> form, final T context) {
        return renderPage(form, context, null)
                .thenApplyAsync(Results::badRequest, HttpExecution.defaultContext());
    }

    default CompletionStage<Result> handleValidForm(final Form<F> form, final T context) {
        final CompletionStage<Result> resultStage = doAction(form.get(), context)
                .thenComposeAsync(result -> handleSuccessfulAction(form.get(), context, result), HttpExecution.defaultContext());
        return recoverWith(resultStage, throwable -> handleFailedAction(form, context, throwable), HttpExecution.defaultContext());
    }

    CompletionStage<? extends R> doAction(final F formData, final T context);

    default CompletionStage<Result> handleFailedAction(final Form<F> form, final T context, final Throwable throwable) {
        final Throwable causeThrowable = throwable.getCause();
        if (causeThrowable instanceof ClientErrorException) {
            return handleClientErrorFailedAction(form, context, (ClientErrorException) causeThrowable);
        }
        return handleGeneralFailedAction(throwable);
    }

    CompletionStage<Result> handleClientErrorFailedAction(final Form<F> form, final T context, final ClientErrorException clientErrorException);

    default CompletionStage<Result> handleGeneralFailedAction(final Throwable throwable) {
        return exceptionallyCompletedFuture(throwable);
    }

    CompletionStage<Result> handleSuccessfulAction(final F formData, final T context, final R result);

    CompletionStage<Html> renderPage(final Form<F> form, final T context, @Nullable final R result);

    default Form<F> createNewFilledForm(final T context) {
        try {
            final F formData = getFormDataClass().getConstructor().newInstance();
            preFillFormData(formData, context);
            final Map<String, String> classFieldValues = BeanUtils.describe(formData);
            final Form<F> filledForm = formFactory().form(getFormDataClass()).bind(classFieldValues);
            filledForm.discardErrors();
            return filledForm;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Missing empty constructor for class " + getFormDataClass().getCanonicalName(), e);
        }
    }

    void preFillFormData(final F formData, final T context);
}
