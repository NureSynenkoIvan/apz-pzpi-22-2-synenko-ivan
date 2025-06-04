import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';

const ArchivePage = () => {
  const { t } = useTranslation();
  const [isLoading, setIsLoading] = useState(false);
  const [restoreDate, setRestoreDate] = useState('');
  const [availableBackups, setAvailableBackups] = useState([]);

  const getAuthHeader = () => {
    const phone = localStorage.getItem('userPhoneNumber');
    const password = localStorage.getItem('userPassword');
    return 'Basic ' + btoa(`${phone}:${password}`);
  };

  useEffect(() => {
    const fetchBackups = async () => {
      try {
        const response = await fetch('http://localhost:8080/archive', {
          method: 'GET',
          headers: {
            'Authorization': getAuthHeader()
          }
        });
        const data = await response.json(); 
        setAvailableBackups(data);
      } catch (error) {
        console.error(t('archive.fetchBackupsError'), error);
      }
    };

    fetchBackups();
  }, [t]);

  const handleBackup = async () => {
    if (!window.confirm(t('archive.confirmBackup'))) return;

    setIsLoading(true);
    try {
      const response = await fetch('http://localhost:8080/archive', {
        method: 'POST',
        headers: {
          'Authorization': getAuthHeader()
        }
      });

      if (response.ok) {
        alert(t('archive.backupSuccess'));
      } else {
        alert(t('archive.backupError'));
      }
    } catch (error) {
      alert(t('archive.genericError', { message: error.message }));
    } finally {
      setIsLoading(false);
    }
  };

  const formatDateForRestore = (dateString) => {
    const date = new Date(dateString);

    const pad = (n) => n.toString().padStart(2, '0');

    return `${date.getFullYear()}_${pad(date.getMonth() + 1)}_${pad(date.getDate())}_${pad(date.getHours())}_${pad(date.getMinutes())}_${pad(date.getSeconds())}`;
  };

  const handleRestore = async () => {
    if (!window.confirm(t('archive.confirmRestore'))) return;

    if (!restoreDate) {
      alert(t('archive.selectDatePrompt'));
      return;
    }


    setIsLoading(true);
    alert(restoreDate)
    try {
      const response = await fetch(`http://localhost:8080/archive/restore?date=${formatDateForRestore(restoreDate)}`, {
        method: 'POST',
        headers: {
          'Authorization': getAuthHeader()
        }
      });

      if (response.ok) {
        alert(t('archive.restoreSuccess'));
      } else {
        alert(t('archive.restoreError'));
      }
    } catch (error) {
      alert(t('archive.genericError', { message: error.message }));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="archive-page">
      <h2>{t('archive.title')}</h2>

      <button onClick={handleBackup} disabled={isLoading}>
        {t('archive.createBackupButton')}
      </button>

      <div style={{ marginTop: '1em' }}>
        <label>
          {t('archive.restoreFromBackupLabel')}:
          <select
            value={restoreDate}
            onChange={(e) => setRestoreDate(e.target.value)}
            style={{ marginLeft: '1em' }}
          >
            <option value="">{t('archive.selectBackupDateOption')}</option>
            {availableBackups.map((isoDate) => (
              <option key={isoDate} value={isoDate}>
                {new Date(isoDate).toLocaleString()}
              </option>
            ))}
          </select>
        </label>

        <button
          onClick={handleRestore}
          disabled={isLoading || !restoreDate}
          style={{ marginLeft: '1em' }}
        >
          {t('archive.restoreButton')}
        </button>
      </div>
    </div>
  );
};

export default ArchivePage;