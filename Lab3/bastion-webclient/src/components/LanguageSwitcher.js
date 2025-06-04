import React, { useState } from 'react';
import { useTranslation } from 'react-i18next'; 


import uaFlag from '../assets/uaFlag.png'
import gbFlag from  '../assets/gbFlag.png'

const LanguageSwitcher = ({ changeLanguage }) => {
  const { i18n } = useTranslation(); 
  const [isLanguageSwitcherDropdownOpen, setIsOpen] = useState(false);

  const currentLanguage = i18n.language;

  const toggleDropdown = () => {
    setIsOpen(!isLanguageSwitcherDropdownOpen);
  };

  const selectLanguage = (lang) => {
    changeLanguage(lang);
    setIsOpen(false);
  };

  const getFlag = (lang) => {
    switch (lang) {
      case 'ua':
        return uaFlag;
      case 'en':
        return gbFlag;
      default:
        return uaFlag; 
    }
  };

  return (
    <div style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center', position: 'relative' }}>
      <button 
        onClick={toggleDropdown} 
        style={{ 
          marginRight: '8px', 
          padding: '8px 12px', 
          cursor: 'pointer', 
          display: 'flex', 
          alignItems: 'center',
          verticalAlign: 'middle' 
        }}
      >
        <img src={getFlag(currentLanguage)} alt={currentLanguage.toUpperCase()} style={{ width: '20px', height: '15px', marginRight: '5px' }} />
        ▼
      </button>
        

      {isLanguageSwitcherDropdownOpen && (
        <ul style={{
          position: 'absolute',
          top: '100%',
          left: 0,
          backgroundColor: '#fff',
          border: '1px solid #ccc',
          borderRadius: '4px',
          listStyle: 'none',
          padding: '0',
          margin: '4px 0 0 0',
          minWidth: '80px',
          zIndex: 1000
        }}>
          <li
            onClick={() => selectLanguage('ua')}
            style={{ padding: '8px 12px', cursor: 'pointer', display: 'flex', alignItems: 'center', '&:hover': { backgroundColor: '#f0f0f0' } }}
          >
            <img src={uaFlag} alt="UA" style={{ width: '20px', height: '15px', marginRight: '5px' }} />
            UA
          </li>
          <li
            onClick={() => selectLanguage('en')}
            style={{ padding: '8px 12px', cursor: 'pointer', display: 'flex', alignItems: 'center', '&:hover': { backgroundColor: '#f0f0f0' } }}
          >
            <img src={gbFlag} alt="EN" style={{ width: '20px', height: '15px', marginRight: '5px' }} />
            EN
          </li>
        </ul>
      )}
    </div>
  );
};

export default LanguageSwitcher;