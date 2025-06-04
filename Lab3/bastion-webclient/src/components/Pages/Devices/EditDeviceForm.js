import React, { useState } from 'react';
import { useTranslation } from 'react-i18next'; 

const EditDeviceForm = ({ device, onSave, onCancel }) => {
  const { t } = useTranslation(); // Ініціалізуємо хук перекладу

  const [formData, setFormData] = useState({
    name: device.name,
    type: device.type,
    latitude: device.latitude,
    longitude: device.longitude,
    online: device.online,
  });

  const handleChange = (e) => {
    const { name, value, type: inputType, checked } = e.target;
    const newValue = inputType === 'checkbox' ? checked : value;
    setFormData((prev) => ({ ...prev, [name]: newValue }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave({ ...device, ...formData });
  };

  return (
    <div className="edit-form">
      <h3>{t('devices.editTitle', { deviceId: device.deviceId })}</h3>
      <form onSubmit={handleSubmit}>
        <label>
          {t('devices.name')}:
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
          />
        </label>

        <label>
          {t('devices.type')}: 
          <input
            type="text"
            name="type"
            value={formData.type}
            onChange={handleChange}
          />
        </label>

        <label>
          {t('devices.latitude')}: 
          <input
            type="number"
            name="latitude"
            value={formData.latitude}
            onChange={handleChange}
          />
        </label>

        <label>
          {t('devices.longitude')}:
          <input
            type="number"
            name="longitude"
            value={formData.longitude}
            onChange={handleChange}
          />
        </label>

        <label>
          {t('devices.online')}: 
          <input
            type="checkbox"
            name="online"
            checked={formData.online}
            onChange={handleChange}
          />
        </label>

        <div className="form-buttons">
          <button type="submit">{t('devices.save')}</button> 
          <button type="button" onClick={onCancel}>{t('devices.cancel')}</button> 
        </div>
      </form>
    </div>
  );
};

export default EditDeviceForm;