import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';


import './App.css'; 

import Employees from './components/Pages/Employees/Employees';
import Devices from './components/Pages/Devices/Devices';
import Login from './components/Pages/Login/Login';
import ShiftQRCode from './components/Pages/QR/ShiftQRCode';
import PrivateRoute from './components/PrivateRoute'; 
import SkyStateViewer from './components/Pages/SkyStateViewer';
import AlarmStatusPage from './components/Pages/AlarmStatusPage';
import ArchivePage from './components/Pages/ArchivePage';

import LanguageSwitcher from './components/LanguageSwitcher';

import './i18n'





function App() {
  const role = localStorage.getItem("userRole");
  const [currentLanguage, setCurrentLanguage] = useState('ua');

  const { i18n, t } = useTranslation();

  const changeLanguage = (lng) => {
    i18n.changeLanguage(lng);
  };
 

  const [isAuthenticated, setIsAuthenticated] = useState(
    localStorage.getItem('isAuthenticated') === 'true'
  );

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
    localStorage.setItem('isAuthenticated', 'true'); 
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    localStorage.removeItem('isAuthenticated'); 
  };

  useEffect(() => {
    const handleStorageChange = () => {
      setIsAuthenticated(localStorage.getItem('isAuthenticated') === 'true');
    };
    window.addEventListener('storage', handleStorageChange);
    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, []);
    
  return (
    <Router>
      <div className="app-container">
        {isAuthenticated && (
          <nav className="navbar">
            <div className="navbar-content">
              <Link to="/employees" className="navbar-brand">
                Bastion
              </Link>

              <ul className="navbar-nav">
                <li className="nav-item">
                  <Link to="/employees">{t('nav.employees')}</Link>
                </li>

                <li className="nav-item">
                  <Link to="/shift">{t('nav.qr')}</Link>
                </li>

                <li className="nav-item">
                  <Link to="/devices">{t('nav.devices')}</Link>
                </li>

                <li className="nav-item">
                  <Link to="/map">{t('nav.sky')}</Link>
                </li>

                {role === "ADMINISTRATOR" && (
                  <li className="nav-item">
                    <Link to="/alarm-status">{t('nav.alarm')}</Link>
                  </li>
                )}
                {role === "ADMINISTRATOR" && (
                  <li className="nav-item">
                    <Link to="/archive">{t('nav.archive')}</Link>
                  </li>
                )}


                <li className="nav-item">
                  <button onClick={handleLogout} className="nav-link-button">
                    {t('nav.logout')}
                    </button>
                </li>
                <li className="nav-item">

                    <header style={{ display: 'flex', justifyContent: 'flex-end', padding: '10px' }}>
                      <LanguageSwitcher changeLanguage={changeLanguage} />
                    </header>
              
                </li>
              </ul>
            </div>
          </nav>
        )}

        <main className="main-content">
          <Routes>
            <Route path="/" element={<Login onLoginSuccess={handleLoginSuccess} />} />
            <Route path="/employees" element={<PrivateRoute><Employees /></PrivateRoute>} />
            <Route path="/devices" element={<PrivateRoute><Devices /></PrivateRoute>} />
            <Route path="/map" element={<PrivateRoute><SkyStateViewer /></PrivateRoute>} />
            <Route path="/alarm-status" element={<PrivateRoute><AlarmStatusPage /></PrivateRoute>} />
            <Route path="/archive" element={<PrivateRoute><ArchivePage /></PrivateRoute>} />
            <Route path="/shift" element={<ShiftQRCode />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
  }

export default App;