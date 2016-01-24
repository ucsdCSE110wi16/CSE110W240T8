package droidsquad.voyage.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import droidsquad.voyage.R;

import static android.Manifest.permission.READ_CONTACTS;


public class SignUpActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    // Id to identity READ_CONTACTS permission request.
    private static final int REQUEST_READ_CONTACTS = 0;

    private final String TAG = SignUpActivity.class.getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private boolean currentlySigningUp = false;

    // UI references.
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mMobileNumberView;
    private Spinner mGenderSpinner;
    private Button mDobButton;
    private View mProgressView;
    private TextView errorView;
    private View mLoginFormView;
    private int mYear;
    private int mMonth;
    private int mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Log.d(TAG, "Sign up Activity opened");

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.signup_email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.signup_password);
        mFirstNameView = (EditText) findViewById(R.id.signup_first_name);
        mLastNameView = (EditText) findViewById(R.id.signup_last_name);
        mMobileNumberView = (EditText) findViewById(R.id.signup_mobile);
        mGenderSpinner = (Spinner) findViewById(R.id.signup_gender_spinner);
        errorView = (TextView) findViewById(R.id.signup_error_text_view);
        errorView.setText("");

        // Setting up the gender spinner
        ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(this,
                R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.genders));

        mGenderSpinner.setAdapter(adapter1);


        // Setting up the DOB Button
        mDobButton = (Button) findViewById(R.id.choose_dob_button);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDate = calendar.get(Calendar.DATE);
        mDobButton.setText(dateFormat.format(calendar.getTime()));
        mDobButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "DOB picker button clicked");

                // show Date picker dialog
                DatePickerDialog dialog = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDate = dayOfMonth;

                        // Set the text value of the button
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DATE, dayOfMonth);

                        mDobButton.setText(dateFormat.format(calendar.getTime()));

                        Log.d(TAG, "DOB changed to " + dateFormat.format(calendar.getTime()));
                    }
                },
                        mYear, mMonth, mDate);

                dialog.show();
            }
        });


        Button mSignUpButton = (Button) findViewById(R.id.signup_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });

        mLoginFormView = findViewById(R.id.signup_form);
        mProgressView = findViewById(R.id.signup_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignup() {
        if (currentlySigningUp) {
            return;
        }

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mMobileNumberView.setError(null);

        // Store values at the time of the login attempt.
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        String mobileNum = mMobileNumberView.getText().toString();
        String gender = mGenderSpinner.getSelectedItem().toString();
        JSONObject dOB = new JSONObject();
        try {
            dOB.put("month", mMonth);
            dOB.put("day", mDate);
            dOB.put("year", mYear);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        String dateOfBirth = dOB.toString();

        boolean cancel = false;
        View focusView = null;

        // Check the validity of Mobile Number
        if (mobileNum.isEmpty()) {
            mMobileNumberView.setError(getString(R.string.error_empty_mobile_num));
            focusView = mMobileNumberView;
            cancel = true;
        } else if (!isMobileNumValid(mobileNum)) {
            mMobileNumberView.setError(getString(R.string.error_invalid_mobile_num));
            focusView = mMobileNumberView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (getPasswordError(password) != null) {
            mPasswordView.setError(getPasswordError(password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check if First and last name are non-empty
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Log.d(TAG, "Attempting to login user: " + firstName + " " + lastName + "\nemail: "
                    + email + "\nGender: " + gender + "\nDOB: " + dateOfBirth);
            currentlySigningUp = true;
            showProgress(true);

            ParseUser user = new ParseUser();
            user.setUsername(email);
            user.setPassword(password);
            user.setEmail(email);
            user.put("mobile", mobileNum);
            user.put("firstName", firstName);
            user.put("lastName", lastName);
            user.put("gender", gender);
            user.put("dateOfBirth", dateOfBirth);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    currentlySigningUp = false;
                    if (e == null) {
                        // User successfully signed up
                        Log.d(TAG, "User successfully signed up");
                        ParseUser.logInInBackground(email, password, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    // Successfully logged user in
                                    Log.d(TAG, "Successfully logged user in.");
                                    // Taking the user to the main activity and
                                    // killing all other activities in the BG

                                    // TODO replace BlahActivity with the main activity
//                                    Intent intent = new Intent(SignUpActivity.this, BlahActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
                                } else {
                                    Log.d(TAG, "Unable to log user in." +
                                            ((e != null) ? (" ParseException occured. Code: " +
                                            e.getCode() + ". Message: " + e.getMessage()) : ""));

                                    // TODO take the user to the 404 (unknown error) page
                                }
                            }
                        });
                    } else {
                        // Excpetion happened
                        Log.d(TAG, "ParseException occured. Code: " + e.getCode()
                                + " Message: " + e.getMessage());

                        if (e.getCode() == 202) {
                            mEmailView.setError(getString(R.string.error_email_already_taken));
                            mEmailView.requestFocus();
                        } else if (e.getCode() == 100) {
                            errorView.setText(getString(R.string.error_no_internet_connection));
                        } else {
                            errorView.setText(e.getMessage());
                        }
                        showProgress(false);
                    }
                }
            });
        }
    }

    private boolean isMobileNumValid(String mobileNum) {
        /*
         *   matching phone number with regex
         *   Examples: Matches following phone numbers:
         *   (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890
         */
        Pattern p = Pattern.compile("^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$");
        Matcher m = p.matcher(mobileNum);
        return m.matches();
    }

    private String getPasswordError(String password) {
        if (password.length() < 8) {
            return getString(R.string.error_short_password);
        }
        Pattern lower = Pattern.compile("[a-z]+");
        Matcher m = lower.matcher(password);
        if (!m.find()) {
            return getString(R.string.error_password_lowercase);
        }
        Pattern num = Pattern.compile("[0-9]+");
        m = num.matcher(password);
        if(!m.find()) {
            return getString(R.string.error_password_number);
        }
        Pattern upper = Pattern.compile("[A-Z]+");
        m = upper.matcher(password);
        if(!m.find()) {
            return getString(R.string.error_password_uppercase);
        }
        return null;
    }

    private boolean isEmailValid(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+"); // matching email with regex
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignUpActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}

