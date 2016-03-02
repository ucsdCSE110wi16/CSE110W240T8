package droidsquad.voyage.model.parseModels;

import com.parse.ParseObject;

import droidsquad.voyage.util.Constants;

public abstract class ParseModel {
    /**
     * Get error message from error code
     *
     * @param errorCode The error code
     * @return The error string
     */
    protected static String getParseErrorString(int errorCode) {
        switch (errorCode) {
            case 100:
                return Constants.ERROR_NO_INTERNET_CONNECTION;
            default:
                return Constants.ERROR_UNKNOWN;
        }
    }

    public interface ParseResponseCallback {
        void onSuccess();

        void onFailure(String error);
    }

    public interface ParseObjectCallback {
        void onSuccess(ParseObject parseObject);

        void onFailure(String error);
    }
}
