import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';

const AlarmStatus = () => {
  const { t } = useTranslation();
  const [alarmData, setAlarmData] = useState({ isAlarm: false });

  useEffect(() => {
    const fetchAlarm = async () => {
      const storedPhoneNumber = localStorage.getItem('userPhoneNumber');
      const storedPassword = localStorage.getItem('userPassword');
      const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);
      const backendUrl = process.env.REACT_APP_BACKEND_URL;

      try {
        const response = await fetch(backendUrl + '/alarm', {
          method: 'GET',
          headers: {
            Authorization: `Basic ${credentials}`,
          },
        });
        const data = await response.json();
        setAlarmData(data);
      } catch (error) {
        console.error(t('alarmStatus.fetchError'), error);
      }
    };

    fetchAlarm();
    const interval = setInterval(fetchAlarm, 1000); 

    return () => clearInterval(interval);
  }, [t]);

  return (
    <div className="alarm-status">
      <h2>{t('alarmStatus.title')}</h2>
      <p>
        {alarmData.isAlarm ? (
          <span style={{ color: 'red' }}>🔴 {t('alarmStatus.active')}</span>
        ) : (
          <span style={{ color: 'green' }}>🟢 {t('alarmStatus.inactive')}</span>
        )}
      </p>
    </div>
  );
};

export default AlarmStatus;