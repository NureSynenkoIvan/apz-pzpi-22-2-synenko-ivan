import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import './Devices.css'; 

function AddDeviceForm({ onSave, onCancel }) {
  const { t } = useTranslation();

  const [device, setDevice] = useState({
    name: '',
    type: '',
    latitude: '',
    longitude: '',
    online: false,
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setDevice((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(device);
  };

  return (
    <form onSubmit={handleSubmit} className="device-form">
      <h3>{t('addDevice.title')}</h3>

      <label>{t('addDevice.deviceId')}:</label>
      <input name="deviceId" value={device.deviceId} onChange={handleChange} required />

      <label>{t('addDevice.name')}:</label>
      <input name="name" value={device.name} onChange={handleChange} required />

      <label>{t('addDevice.type')}:</label>
      <input name="type" value={device.type} onChange={handleChange} required />

      <label>{t('addDevice.latitude')}:</label>
      <input name="latitude" type="number" value={device.latitude} onChange={handleChange} required />

      <label>{t('addDevice.longitude')}:</label>
      <input name="longitude" type="number" value={device.longitude} onChange={handleChange} required />

      <label>
        {t('addDevice.online')}:
        <input name="online" type="checkbox" checked={device.online} onChange={handleChange} />
      </label>

      <button type="submit">{t('addDevice.save')}</button>
      <button type="button" onClick={onCancel}>{t('addDevice.cancel')}</button>
    </form>
  );
}

export default AddDeviceForm;
