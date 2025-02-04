package com.company.onboarding.view.component.loginform;


import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import io.jmix.core.MessageTools;
import io.jmix.core.security.AccessDeniedException;
import io.jmix.flowui.FlowuiLoginProperties;
import io.jmix.flowui.component.loginform.JmixLoginForm;
import io.jmix.flowui.kit.component.FlowuiComponentUtils;
import io.jmix.flowui.kit.component.loginform.EnhancedLoginForm;
import io.jmix.flowui.kit.component.loginform.JmixLoginI18n;
import io.jmix.flowui.view.*;
import io.jmix.securityflowui.authentication.AuthDetails;
import io.jmix.securityflowui.authentication.LoginViewSupport;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

@Route(value = "LoginFormView", layout = MainView.class)
@ViewController("LoginFormView")
@ViewDescriptor("login-form-view.xml")
public class LoginFormView extends StandardView {


    // tag::login-view-support[]

    @Autowired
    private LoginViewSupport loginViewSupport;
    // end::login-view-support[]

    @Autowired
    private MessageTools messageTools;

    // tag::login-form[]
    @ViewComponent
    private JmixLoginForm loginForm;
    // end::login-form[]

    @Value("${ui.login.defaultUsername:}")
    private String defaultUsername;

    @Value("${ui.login.defaultPassword:}")
    private String defaultPassword;

    @Subscribe
    public void onInit(InitEvent event) {
        initLocales();
        initDefaultCredentials();
    }

    protected void initLocales() {
        FlowuiComponentUtils.setItemsMap(loginForm,
                MapUtils.invertMap(messageTools.getAvailableLocalesMap()));

        loginForm.setSelectedLocale(VaadinSession.getCurrent().getLocale());
    }

    protected void initDefaultCredentials() {
        if (StringUtils.isNotBlank(defaultUsername)) {
            loginForm.setUsername(defaultUsername);
        }

        if (StringUtils.isNotBlank(defaultPassword)) {
            loginForm.setPassword(defaultPassword);
        }
    }

    // tag::on-login-handler[]
    @Subscribe("loginForm")
    public void onLogin(AbstractLogin.LoginEvent event) {
        try {
            loginViewSupport.authenticate(
                    AuthDetails.of(event.getUsername(), event.getPassword())
                            .withLocale(loginForm.getSelectedLocale()) // <1>
                            .withRememberMe(loginForm.isRememberMe()) // <2>
            );
        } catch (BadCredentialsException | DisabledException | LockedException | AccessDeniedException e) {
            event.getSource().setError(true);
        }
    }
    // end::on-login-handler[]


    // tag::on-locale-changed[]
    @Autowired
    private MessageBundle messageBundle;

    @Subscribe("loginForm")
    public void onLoginFormLocaleChanged(EnhancedLoginForm.LocaleChangedEvent event) {
        UI.getCurrent().getPage().setTitle(messageBundle.getMessage("LoginView.title"));

        final JmixLoginI18n loginI18n = JmixLoginI18n.createDefault();

        final JmixLoginI18n.JmixForm form = new JmixLoginI18n.JmixForm();
        form.setTitle(messageBundle.getMessage("loginForm.headerTitle"));
        form.setUsername(messageBundle.getMessage("loginForm.username"));
        form.setPassword(messageBundle.getMessage("loginForm.password"));
        form.setSubmit(messageBundle.getMessage("loginForm.submit"));
        form.setForgotPassword(messageBundle.getMessage("loginForm.forgotPassword"));
        form.setRememberMe(messageBundle.getMessage("loginForm.rememberMe"));
        loginI18n.setForm(form);

        final LoginI18n.ErrorMessage errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setTitle(messageBundle.getMessage("loginForm.errorTitle"));
        errorMessage.setMessage(messageBundle.getMessage("loginForm.badCredentials"));
        loginI18n.setErrorMessage(errorMessage);

        loginForm.setI18n(loginI18n);
    }
    // end::on-locale-changed[]
}