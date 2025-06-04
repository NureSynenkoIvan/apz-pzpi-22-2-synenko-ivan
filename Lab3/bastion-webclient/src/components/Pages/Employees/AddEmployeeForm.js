import React, { useState } from 'react';
import { useTranslation } from 'react-i18next'; // Імпортуємо useTranslation

function AddEmployeeForm({ onSave, onCancel }) {
  const { t } = useTranslation(); 
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
    role: 'DISPATCHER',
    position: '',
    workDays: [],
    shiftStart: [8, 0],
    shiftFinish: [16, 0],
    onDuty: false,
    passwordHash: '',
  });

  const allDays = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    if (name === 'onDuty') {
      setFormData({ ...formData, [name]: checked });
    } else if (name === 'workDays') {
      const updatedDays = checked
        ? [...formData.workDays, value]
        : formData.workDays.filter(day => day !== value);
      setFormData({ ...formData, workDays: updatedDays });
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };

  const handleTimeChange = (field, index, value) => {
    const updated = [...formData[field]];
    updated[index] = parseInt(value);
    setFormData({ ...formData, [field]: updated });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const employee = {
      ...formData,
      workTime: {
        workDays: formData.workDays,
        shiftStart: formData.shiftStart,
        shiftFinish: formData.shiftFinish,
      },
      location: null,
      passwordHash: formData.passwordHash, 
    };

    onSave(employee);
  };

  return (
    <form className="add-form" onSubmit={handleSubmit}>
      <h3>{t('employees.addTitle')}</h3> 
      <input 
        name="firstName" 
        placeholder={t('employees.firstNamePlaceholder')} 
        value={formData.firstName} 
        onChange={handleChange} 
        required 
      />
      <input 
        name="lastName" 
        placeholder={t('employees.lastNamePlaceholder')} 
        value={formData.lastName} 
        onChange={handleChange} 
        required 
      />
      <input 
        name="phoneNumber" 
        placeholder={t('employees.phoneNumberPlaceholder')} 
        value={formData.phoneNumber} 
        onChange={handleChange} 
        required 
      />
      
      <select name="role" value={formData.role} onChange={handleChange}>
        <option value="DISPATCHER">{t('roles.dispatcher')}</option>
        <option value="ADMINISTRATOR">{t('roles.administrator')}</option>
        <option value="USER">{t('roles.user')}</option>
      </select>
      
      <input 
        name="position" 
        placeholder={t('employees.positionPlaceholder')}
        value={formData.position} 
        onChange={handleChange} 
      />
      
      <fieldset>
        <legend>{t('employees.workDaysLegend')}</legend> 
        {allDays.map(day => (
          <label key={day}>
            <input
              type="checkbox"
              name="workDays"
              value={day}
              checked={formData.workDays.includes(day)}
              onChange={handleChange}
            />
            {t(`days.${day.toLowerCase()}`)} 
          </label>
        ))}
      </fieldset>

      <div>
        <label>{t('employees.shiftStartLabel')}:</label> 
        <input type="number" min="0" max="23" value={formData.shiftStart[0]} onChange={(e) => handleTimeChange('shiftStart', 0, e.target.value)} />
        <input type="number" min="0" max="59" value={formData.shiftStart[1]} onChange={(e) => handleTimeChange('shiftStart', 1, e.target.value)} />
      </div>

      <div>
        <label>{t('employees.shiftFinishLabel')}:</label> 
        <input type="number" min="0" max="23" value={formData.shiftFinish[0]} onChange={(e) => handleTimeChange('shiftFinish', 0, e.target.value)} />
        <input type="number" min="0" max="59" value={formData.shiftFinish[1]} onChange={(e) => handleTimeChange('shiftFinish', 1, e.target.value)} />
      </div>

      <label>
        <input type="checkbox" name="onDuty" checked={formData.onDuty} onChange={handleChange} />
        {t('employees.onDutyLabel')} 
      </label>

      <input 
        name="passwordHash" 
        placeholder={t('employees.passwordHashPlaceholder')} 
        value={formData.passwordHash} 
        onChange={handleChange} 
      />

      <div className="form-buttons">
        <button type="submit">{t('devices.save')}</button> 
        <button type="button" onClick={onCancel}>{t('devices.cancel')}</button> 
      </div>
    </form>
  );
}

export default AddEmployeeForm;