import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import AlarmStatus from '../AlarmStatus';

const AlarmStatusPage = () => {
  const { t } = useTranslation();
  const [alarmData, setAlarmData] = useState({ isAlarm: false });
  const [isSending, setIsSending] = useState(false);

  const storedPhoneNumber = localStorage.getItem('userPhoneNumber');
  const storedPassword = localStorage.getItem('userPassword');
  const userRole = localStorage.getItem('userRole');
  const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);

  const fetchAlarm = async () => {
    
    try {
      const response = await fetch('http://localhost:8080/alarm', {
        method: "GET",
        headers: {
          "Authorization": `Basic ${credentials}`
        }
      });
      const data = await response.json();
      setAlarmData(data);
    } catch (error) {
      console.error(t('alarmStatus.fetchError'), error);
    }
  };

  const toggleAlarm = async (newState) => {
    const confirmMessage = newState
      ? t('alarmStatus.confirmActivate')
      : t('alarmStatus.confirmDeactivate');

    const confirmed = window.confirm(confirmMessage);
    if (!confirmed) return;

    setIsSending(true);
    try {
      const response = await fetch(`http://localhost:8080/alarm?isAlarm=${newState}`, {
        method: "POST",
        headers: {
          "Authorization": `Basic ${credentials}`
        }
      });
      const data = await response.json();
      setAlarmData(data);
    } catch (error) {
      console.error(t('alarmStatus.toggleError'), error);
    } finally {
      setIsSending(false);
    }
  };

  useEffect(() => {
    fetchAlarm();
    const interval = setInterval(fetchAlarm, 2000);
    return () => clearInterval(interval);
  }, [t]);

  if (!alarmData) return <p>{t('alarmStatus.loading')}</p>;

  return (
    <div className="alarm-status-page">
      <div>
        <AlarmStatus></AlarmStatus>
      </div>

      {userRole === "ADMINISTRATOR" && (
        <button
          onClick={() => toggleAlarm(!alarmData.isAlarm)}
          disabled={isSending}
          style={{
            backgroundColor: alarmData.isAlarm ? '#dc3545' : '#28a745',
            color: 'white',
            padding: '10px 20px',
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer',
            marginBottom: '1rem'
          }}
        >
          {alarmData.isAlarm ? t('alarmStatus.deactivateButton') : t('alarmStatus.activateButton')}
        </button>
      )}

      {alarmData.isAlarm && alarmData.currentEvent && (
        <div className="alarm-details">
          <p><strong>{t('alarmStatus.alarmTime')}:</strong> {new Date(alarmData.currentEvent.alarmTime).toLocaleString()}</p>
          <p><strong>{t('alarmStatus.threatCount')}:</strong> {alarmData.currentEvent.threateningObjects.length}</p>
          <ul>
            {alarmData.currentEvent.threateningObjects && alarmData.currentEvent.threateningObjects.map((obj, index) => (
              <li key={index}>
                {obj.name} ({obj.latitude}, {obj.longitude}, {obj.altitude})
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default AlarmStatusPage;