
import React, { useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import LoginSignUp from './Components/LoginSignup/LoginSignUp';
import Dashboard from './Components/Dashboard/Dashboard';
import PrivateRoute from './Components/PrivateRoute';

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('jwtToken'));

  const handleLogin = () => {
    setIsLoggedIn(true);
  };

  return (
    <Routes>
      <Route 
        path="/" 
        element={
          isLoggedIn ? <Navigate to="/dashboard" /> : <LoginSignUp onLogin={handleLogin} />
        } 
      />

      {/* Dashboard route protected by PrivateRoute */}
      <Route 
        path="/dashboard" 
        element={
          <PrivateRoute isLoggedIn={isLoggedIn}>
            <Dashboard setIsLoggedIn={setIsLoggedIn} />
          </PrivateRoute>
        } 
      />
    </Routes>
  );
};

export default App;