/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gecko.background.fxa;

import org.mozilla.gecko.sync.ExtendedJSONObject;
import org.mozilla.gecko.sync.HTTPFailureException;
import org.mozilla.gecko.sync.net.SyncStorageResponse;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;

/**
 * From <a href="https://github.com/mozilla/fxa-auth-server/blob/master/docs/api.md">https://github.com/mozilla/fxa-auth-server/blob/master/docs/api.md</a>.
 */
public class FxAccountClientException extends Exception {
  private static final long serialVersionUID = 7953459541558266597L;

  public FxAccountClientException(String detailMessage) {
    super(detailMessage);
  }

  public FxAccountClientException(Exception e) {
    super(e);
  }

  public static class FxAccountClientRemoteException extends FxAccountClientException {
    private static final long serialVersionUID = 2209313149952001097L;

    public final HttpResponse response;
    public final long httpStatusCode;
    public final long apiErrorNumber;
    public final String error;
    public final String message;
    public final String info;
    public final ExtendedJSONObject body;

    public FxAccountClientRemoteException(HttpResponse response, long httpStatusCode, long apiErrorNumber, String error, String message, String info, ExtendedJSONObject body) {
      super(new HTTPFailureException(new SyncStorageResponse(response)));
      if (body == null) {
        throw new IllegalArgumentException("body must not be null");
      }
      this.response = response;
      this.httpStatusCode = httpStatusCode;
      this.apiErrorNumber = apiErrorNumber;
      this.error = error;
      this.message = message;
      this.info = info;
      this.body = body;
    }

    @Override
    public String toString() {
      return "<FxAccountClientRemoteException " + this.httpStatusCode + " [" + this.apiErrorNumber + "]: " + this.message + ">";
    }

    public boolean isInvalidAuthentication() {
      return httpStatusCode == HttpStatus.SC_UNAUTHORIZED;
    }

    public boolean isUnverified() {
      return apiErrorNumber == FxAccountRemoteError.ATTEMPT_TO_OPERATE_ON_AN_UNVERIFIED_ACCOUNT;
    }

    public boolean isUpgradeRequired() {
      return
          apiErrorNumber == FxAccountRemoteError.ENDPOINT_IS_NO_LONGER_SUPPORTED ||
          apiErrorNumber == FxAccountRemoteError.INCORRECT_LOGIN_METHOD_FOR_THIS_ACCOUNT ||
          apiErrorNumber == FxAccountRemoteError.INCORRECT_KEY_RETRIEVAL_METHOD_FOR_THIS_ACCOUNT ||
          apiErrorNumber == FxAccountRemoteError.INCORRECT_API_VERSION_FOR_THIS_ACCOUNT;
    }

  }

  public static class FxAccountClientMalformedResponseException extends FxAccountClientRemoteException {
    private static final long serialVersionUID = 2209313149952001098L;

    public FxAccountClientMalformedResponseException(HttpResponse response) {
      super(response, 0, FxAccountRemoteError.UNKNOWN_ERROR, "Response malformed", "Response malformed", "Response malformed", new ExtendedJSONObject());
    }
  }
}
