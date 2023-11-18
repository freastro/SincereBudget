(function () {
    // Detect login form
    let forms = Array.from(document.getElementsByTagName('form'))
            .filter(el => el.checkVisibility());
    if (forms.length === 1) {
        // TODO login
        return {state: 'checkLogin'};
    } else if (forms.length > 1) {
        return {state: 'error', message: 'Too many login forms'};
    } else {
        return {state: 'login'};
    }
})
