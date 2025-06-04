import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import './Devices.css'; 
import EditDeviceForm from './EditDeviceForm';
import AddDeviceForm from './AddDeviceForm';

function Devices() {
  const { t } = useTranslation();
  const [editingDevice, setEditingDevice] = useState(null); 
  const [devicesData, setDevicesData] = useState(null); 
  const [loading, setLoading] = useState(true);
  const [isAdding, setIsAdding] = useState(false);        
  const [error, setError] = useState(null);             

  const backendEndpoint = 'http://localhost:8080/devices';

  useEffect(() => {
    const fetchDevices = async () => {
      const storedPhoneNumber = localStorage.getItem('userPhoneNumber'); 
      const storedPassword = localStorage.getItem('userPassword');
      
      if (!storedPhoneNumber || !storedPassword) {
        setError(t('devices.authError'));
        setLoading(false);
        return;
      }

      const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);
      try {
        const response = await fetch(backendEndpoint, {
          method: "GET",
          headers: {
              "Authorization": `Basic ${credentials}`,
              "Content-Type": "application/json"
          }});

        if (response.ok) {
          const data = await response.json(); 
          setDevicesData(data); 
        } else {
          const errorData = await response.json().catch(() => response.text()); 
          setError(t('devices.loadError', { status: response.status, message: JSON.stringify(errorData) }));
        }
      } catch (err) {
        setError(t('devices.networkError', { message: err.message }));
      } finally {
        setLoading(false); 
      }
    };

    fetchDevices(); 
  }, [t]);

  if (loading) {
    return (
      <div className="devices-page">
        <h2>{t('devices.loading')}</h2>
        <p>{t('devices.wait')}</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="devices-page error-state">
        <h2>{t('devices.loadFail')}</h2>
        <p>{error}</p>
        <p>{t('devices.tryAgain')}</p>
      </div>
    );
  }

  const role = localStorage.getItem('userRole');
  const handleEdit = (device) => setEditingDevice(device);

  const handleSave = async (updatedDevice) => {
    try {
      const storedPhoneNumber = localStorage.getItem('userPhoneNumber'); 
      const storedPassword = localStorage.getItem('userPassword');
      const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);

      const response = await fetch(backendEndpoint, {
        method: 'PUT',
        headers: {
          'Authorization': `Basic ${credentials}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedDevice),
      });

      if (response.ok) {
        setDevicesData(prevDevices =>
          prevDevices.map(dev =>
            dev.deviceId === updatedDevice.deviceId ? updatedDevice : dev
          )
        );
        alert(t('devices.updateSuccess'));
      } else {
        const errorText = await response.text();
        alert(t('devices.updateError', { message: errorText }));
      }
    } catch (err) {
      alert(t('devices.networkError', { message: err.message }));
    } finally {
      setEditingDevice(null);
    }
  };

  const handleDelete = async (device) => {
    if (!window.confirm(t('devices.confirmDelete'))) return;

    try {
      const storedPhoneNumber = localStorage.getItem('userPhoneNumber'); 
      const storedPassword = localStorage.getItem('userPassword');
      const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);

      const response = await fetch(backendEndpoint, {
        method: 'DELETE',
        headers: {
          'Authorization': `Basic ${credentials}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(device),
      });

      if (response.ok) {
        setDevicesData(prevDevices =>
          prevDevices.filter(dev => dev.deviceId !== device.deviceId)
        );
        alert(t('devices.deleteSuccess'));
      } else {
        const errorText = await response.text();
        alert(t('devices.deleteError', { message: errorText }));
      }
    } catch (err) {
      alert(t('devices.networkError', { message: err.message }));
    }
  };

  const handleAdd = async (newDevice) => {
    try {
      const storedPhoneNumber = localStorage.getItem('userPhoneNumber'); 
      const storedPassword = localStorage.getItem('userPassword');
      const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);

      const transformedDevice = {
        name: newDevice.name,
        type: newDevice.type,
        online: newDevice.online,
        location: {
          latitude: parseFloat(newDevice.latitude),
          longitude: parseFloat(newDevice.longitude)
        }
      };

      const response = await fetch(backendEndpoint, {
        method: 'POST',
        headers: {
          'Authorization': `Basic ${credentials}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(transformedDevice),
      });

      if (response.ok) {
        setDevicesData((prev) => [...prev, newDevice]);
        alert(t('devices.addSuccess'));
      } else {
        const errorText = await response.text();
        alert(t('devices.addError', { message: errorText }));
      }
    } catch (err) {
      alert(t('devices.networkError', { message: err.message }));
    } finally {
      setIsAdding(false);
    }
  };

  return (
    <div className="devices-page">
      <h2>{t('devices.title')}</h2>

      {role === 'ADMINISTRATOR' && !isAdding && (
        <button onClick={() => setIsAdding(true)}>+ {t('devices.add')}</button>
      )}

      {isAdding && (
        <AddDeviceForm
          onSave={handleAdd}
          onCancel={() => setIsAdding(false)}
        />
      )}

      {editingDevice && (
        <EditDeviceForm
          device={editingDevice}
          onSave={handleSave}
          onCancel={() => setEditingDevice(null)}
        />
      )}

      {devicesData && devicesData.length > 0 ? (
        <table className="device-table">
          <thead>
            <tr>
              <th>{t('devices.id')}</th>
              <th>{t('devices.name')}</th>
              <th>{t('devices.type')}</th>
              <th>{t('devices.latitude')}</th>
              <th>{t('devices.longitude')}</th>
              <th>{t('devices.online')}</th>
              {role === 'ADMINISTRATOR' && <th>{t('devices.actions')}</th>}
            </tr>
          </thead>
          <tbody>
            {devicesData.map((device) => (
              <tr key={device.deviceId}>
                <td>{device.deviceId}</td>
                <td>{device.name}</td>
                <td>{device.type}</td>
                <td>{device.latitude}</td>
                <td>{device.longitude}</td>
                <td>{device.online ? t('devices.yes') : t('devices.no')}</td>
                {role === 'ADMINISTRATOR' && (
                  <td>
                    <button onClick={() => handleEdit(device)}>{t('devices.edit')}</button>{' '}
                    <button onClick={() => handleDelete(device)}>{t('devices.delete')}</button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>{t('devices.noData')}</p>
      )}
    </div>
  );
}

export default Devices;
