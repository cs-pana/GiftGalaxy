/*document.addEventListener('DOMContentLoaded', function () {
    const signupForm = document.querySelector('section');
    signupForm.style.opacity = 0;
  
    setTimeout(() => {
      signupForm.style.transition = 'opacity 1s ease-in-out';
      signupForm.style.opacity = 1;
    }, 500);

    const signupButton = document.querySelector('button[type="submit"]');
    signupButton.addEventListener('click', function (event) {
        const emailInput = document.querySelector('input[name="email"]');
        const passwordInput = document.querySelector('input[name="password"]');
        const confirmPasswordInput = document.querySelector('input[name="confirm-password"]');

        // Custom validation logic
        const emailIsValid = validateEmail(emailInput.value);
        const passwordIsValid = validatePassword(passwordInput.value);
        const passwordsMatch = passwordInput.value === confirmPasswordInput.value;

        if (!emailIsValid) {
            alert('Please enter a valid email address.');
            emailInput.focus();
            event.preventDefault(); // Prevent form submission
            return;
        }

        if (!passwordIsValid) {
            alert('Password must be at least 8 characters long, and include at least one uppercase letter, one lowercase letter, and one number.');
            passwordInput.focus();
            event.preventDefault(); // Prevent form submission
            return;
        }

        if (!passwordsMatch) {
            alert('Passwords do not match. Please confirm your password.');
            confirmPasswordInput.focus();
            event.preventDefault(); // Prevent form submission
            return;
        }
    });

    function validateEmail(email) {
        // Basic email validation regex
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailPattern.test(email);
    }

    function validatePassword(password) {
        // Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number
        const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
        return passwordPattern.test(password);
    }
    
});*/

document.addEventListener('DOMContentLoaded', function () {
    const signupForm = document.querySelector('form#signupForm');
    signupForm.addEventListener('submit', function (event) {
        event.preventDefault(); // Prevent default form submission

        const username = document.querySelector('input[name="username"]').value;
        const surname = document.querySelector('input[name="surname"]').value;
        const email = document.querySelector('input[name="email"]').value;
        const password = document.querySelector('input[name="password"]').value;
        const confirmPassword = document.querySelector('input[name="confirm-password"]').value;

        // Validation
        if (password !== confirmPassword) {
            alert('Passwords do not match.');
            return;
        }

        if (!validateEmail(email)) {
            alert('Invalid email format.');
            return;
        }

        if (!validatePassword(password)) {
            alert('Password does not meet the requirements.');
            return;
        }

        // Prepare JSON data
        const data = {
            username,
            surname,
            email,
            password
        };

        fetch('/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (response.ok) {
                alert('Registration successful. Redirecting to login page.');
                //signupForm.reset(); // Optional: clear form fields after submission
                window.location.href = "/login";
            } else {
                alert('Registration failed');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    });

    function validateEmail(email) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailPattern.test(email);
    }

    function validatePassword(password) {
        const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
        return passwordPattern.test(password);
    }
});