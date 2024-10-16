import React, { useState } from 'react';
import './LoginSignUp.css';
import axiosInstance, { switchToAuthService } from '../axiosInstance'; 
import { useNavigate } from 'react-router-dom';
import { auth,provider } from './firebase';
import { signInWithPopup } from 'firebase/auth';
import axios from 'axios';

import user_icon from '../Assets/person icon.png';
import email_icon from '../Assets/email icon.png';
import password_icon from '../Assets/password icon.png';
import logo from '../Assets/logo3.png';
import googleloginimage from '../Assets/google-login.png'

const LoginSignup = ({ onLogin }) => { // Use onLogin to pass login handler
  const [isLogin, setIsLogin] = useState(true);  // Switch between login and signup
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [username, setUsername] = useState('');  // For signup only
  const [surname, setSurname] = useState('');    // For signup only
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');    // For signup confirmation
  const navigate = useNavigate();

  // Handle login
  const handleLogin = async (e) => {

    switchToAuthService();
    e.preventDefault();
    setError('');
    try {
      const response = await axiosInstance.post('/login', {
        username: email,
        password: password
      });

      const token = response.data; // Extract the JWT token
      if (token) {
        localStorage.setItem('jwtToken', token); // Store the token in localStorage

        // Update login state
        onLogin(); // Call the onLogin handler passed from parent (App.js)
        navigate('/dashboard');
      } else {
        setError('Login failed: Token not received.');
      }

    } catch (err) {
      setError('Invalid login credentials. Please try again.');
    }
  };

  // Handle signup
  const handleSignup = async (e) => {
    switchToAuthService();
    e.preventDefault();

    setError('');
    try {
      const response = await axiosInstance.post('/signup', {
        email,
        password,
        username,
        surname
      });


      setSuccess('Registration successful! Please log in.');
      setIsLogin(true);  // After signup, return to login screen
      
      setUsername('');
      setSurname('');
    } catch (err) {
      // Check if the error has a response from the server
      if (err.response && err.response.data) {
        const errorMessage = err.response.data;
        setError(errorMessage);
      } else {
        // Fallback error message if no specific message is returned
        setError('Registration failed. Please try again.');
      }
    }
  };

  // login with google
  const handleGoogleLogin = async () => {
    try {

      console.log('Attempting Google Login...');
      const result = await signInWithPopup(auth, provider);

      if(result) {
      const user = result.user;
      console.log('Google login successful:', user);

      //put the token returned by firebase into local storage
      const idToken = await user.getIdToken();
      if (idToken) {
        localStorage.setItem('firebaseToken', idToken);
      } else {
        console.error('token not received from firebase.');
      }

      //Send the firebase id token to authentication service to sign up/log in in exchange for a jwt token
      const axiosFirebase = axios.create({
        baseURL: 'http://localhost:8080',
        headers: {
            'Content-Type': 'application/json',
          }
        });   
      const response = await axiosFirebase.post('/google-signup', { idToken });
      
      if (response.data) {
        const jwtToken = response.data;  // The JWT token returned by the backend
        localStorage.setItem('jwtToken', jwtToken);  // Save JWT token in localStorage
        //console.log('JWT Token:', jwtToken);

        navigate('/dashboard');
      } else {
        console.error('JWT token not received.');
      }
    }
  } catch (err) {
    console.error('Error in Google Login:', err);
  }
};

  return (
    <div>
      <div className="logo">
        <img src={logo} alt="Logo" className="logo-image" />
      </div>

      <div className="container">
        <div className="header">
          <h2 className="text">{isLogin ? 'Login' : 'Signup'}</h2>
          <div className="underline"></div>
        </div>

        {error && <p style={{ color: 'red' }}>{error}</p>}
        {success && <p style={{ color: 'green' }}>{success}</p>}

        <form className="inputs" onSubmit={isLogin ? handleLogin : handleSignup}>
          {!isLogin && (
            <>
              <div className="input">
                <img src={user_icon} alt="" />
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Name"
                  required
                />
              </div>
              <div className="input">
                <img src={user_icon} alt="" />
                <input
                  type="text"
                  value={surname}
                  onChange={(e) => setSurname(e.target.value)}
                  placeholder="Surname"
                  required
                />
              </div>
            </>
          )}

          <div className="input">
            <img src={email_icon} alt="Email Icon" />
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Email"
              required
            />
          </div>

          <div className="input">
            <img src={password_icon} alt="Password Icon" />
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Password"
              required
            />
          </div>

          

          <div className="submit-container">
            <button className="submit" type="submit">
              {isLogin ? 'Login' : 'Signup'}
            </button>
          </div>
        </form>

        <div className="submit-container">
          <button className="submit gray" onClick={() => { 
            setIsLogin(!isLogin);
            setError('');
            setSuccess(''); 
          }}>
            {isLogin ? 'Signup' : 'Login'}
          </button>
        </div>
        <div className="Google-login">
          <button className="google-login-butt" onClick={handleGoogleLogin}>
            Login with Google
          </button>
        </div>
      </div>
    </div>
  );
};

export default LoginSignup;
