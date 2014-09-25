package co.com.zeitgeist.prodactiveapp.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import android.widget.Button;
import android.widget.EditText;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import co.com.zeitgeist.prodactiveapp.R;
import co.com.zeitgeist.prodactiveapp.activity.LoginActivity;



/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

//@RunWith(co.com.zeitgeist.prodactiveapp.test.RobolectricGradleTestRunner.class)
//@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)
public class ApplicationTest  {

    private LoginActivity activity;

    @Before
    public void setup()  {
        activity = Robolectric
                .buildActivity(LoginActivity.class)
                .create()
                .get();
    }

    @Test
    public void clickButton()
    {
        String hello="Hello World!";
        assertThat(hello, equalTo("Hello World!"));
        Button btn= (Button) activity.findViewById(R.id.email_sign_in_button);
        btn.performClick();
        EditText user=(EditText) activity.findViewById(R.id.user);
        Assert.assertNotNull(user);
    }
}