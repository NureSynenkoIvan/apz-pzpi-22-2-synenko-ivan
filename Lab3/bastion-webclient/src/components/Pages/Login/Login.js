import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Login.css';
import { useTranslation } from 'react-i18next';

function Login({ onLoginSuccess }) {
  const { t } = useTranslation();
  const [phoneNumber, setPhoneNumber] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate(); 

  const backendEndpoint = 'http://localhost:8080/login';

  const handleSubmit = async (e) => {
    e.preventDefault(); 
    setError(''); 

    const credentials = btoa(`${phoneNumber}:${password}`); 

    try {
      const response = await fetch(`${backendEndpoint}?phoneNumber=${encodeURIComponent(phoneNumber)}&password=${encodeURIComponent(password)}`, {
        method: 'POST',
        headers: {
          'Authorization': `Basic ${credentials}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) { 
        alert(t('login.successMessage'));
        localStorage.setItem('isAuthenticated', 'true');
        
        localStorage.setItem('userPhoneNumber', phoneNumber); 
        localStorage.setItem('userPassword', password); 

        const data = await response.json();
        const role = data.role;           
        localStorage.setItem('userRole', role); 

        if (onLoginSuccess) {
          onLoginSuccess();
        }

        navigate('/employees'); 
      } else {
        const errorData = await response.json(); 
        setError(errorData.message || t('login.authError'));
        localStorage.removeItem('isAuthenticated'); 
        localStorage.removeItem('userPhoneNumber');
        localStorage.removeItem('userPassword');
      }
    } catch (err) {
      setError(t('login.connectionError'));
      console.error('Login error:', err);
      localStorage.removeItem('isAuthenticated');
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit} className="login-form">
        <h2>{t('login.title')}</h2>
        {error && <p className="error-message">{error}</p>}
        <div className="form-group">
          <label htmlFor="username">{t('login.phoneNumberLabel')}:</label>
          <input
            type="text"
            id="username"
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">{t('login.passwordLabel')}:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="login-button">{t('login.buttonText')}</button>
      </form>
    </div>
  );
}

export default Login;