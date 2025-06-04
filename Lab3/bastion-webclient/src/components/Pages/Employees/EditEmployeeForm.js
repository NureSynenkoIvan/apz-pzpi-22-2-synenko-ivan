import React, { useState } from 'react';
import { useTranslation } from 'react-i18next'; // Імпортуємо useTranslation

const EditEmployeeForm = ({ employee, onSave, onCancel }) => {
  const { t } = useTranslation(); // Ініціалізуємо хук перекладу

  const [formData, setFormData] = useState({
    firstName: employee.firstName || '',
    lastName: employee.lastName || '',
    phoneNumber: employee.phoneNumber || '',
    role: employee.role || '',
    position: employee.position || '',
    // Зверніть увагу: `shiftStart` і `shiftFinish` очікують масив `[години, хвилини]`,
    // але в початковому коді вони ініціалізуються порожнім рядком, якщо немає даних.
    // Якщо вони зберігаються як рядок 'HH:MM', то це нормально.
    // Якщо вони зберігаються як масив, то слід перетворити їх на рядок для input type="time".
    // Тут припускається, що ви зберігаєте їх як рядок 'HH:MM'.
    shiftStart: employee.workTime?.shiftStart || '', 
    shiftFinish: employee.workTime?.shiftFinish || '',
    workDays: employee.workTime?.workDays || [],
    onDuty: employee.onDuty || false,
    applicationRole: employee.applicationRole || '',
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    const newValue = type === 'checkbox' ? checked : value;
    setFormData((prev) => ({ ...prev, [name]: newValue }));
  };

  const handleWorkDaysChange = (e) => {
    const { value, checked } = e.target;
    setFormData((prev) => {
      const updatedDays = checked
        ? [...prev.workDays, value]
        : prev.workDays.filter((day) => day !== value);
      return { ...prev, workDays: updatedDays };
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const updatedEmployee = {
      ...employee,
      firstName: formData.firstName,
      lastName: formData.lastName,
      phoneNumber: formData.phoneNumber,
      role: formData.role, 
      position: formData.position,
      onDuty: formData.onDuty,
      applicationRole: formData.applicationRole,
      fullName: `${formData.firstName} ${formData.lastName}`,
      workTime: {
        shiftStart: formData.shiftStart, 
        shiftFinish: formData.shiftFinish,
        workDays: formData.workDays,
      },
    };
    onSave(updatedEmployee);
  };

  const allDays = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

  return (
    <div className="edit-form">
      <h3>{t('employees.editUserTitle')}</h3> 
      <form onSubmit={handleSubmit}>
        <label>
          {t('employees.firstName')}:
          <input
            type="text"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
          />
        </label>

        <label>
          {t('employees.lastName')}: 
          <input
            type="text"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
          />
        </label>

        <label>
          {t('employees.phoneNumber')}:
          <input
            type="text"
            name="phoneNumber"
            value={formData.phoneNumber}
            onChange={handleChange}
          />
        </label>

        <label>
          {t('employees.position')}: 
          <input
            type="text"
            name="position"
            value={formData.position}
            onChange={handleChange}
          />
        </label>

        <label>
          {t('employees.applicationRole')}: 
          <select
            name="applicationRole"
            value={formData.applicationRole}
            onChange={handleChange}
          >
            <option value="USER">{t('roles.user')}</option>
            <option value="DISPATCHER">{t('roles.dispatcher')}</option>
            <option value="ADMINISTRATOR">{t('roles.administrator')}</option>
          </select>
        </label>

        <label>
          {t('employees.shiftStartLabel')}: 
          <input
            type="time"
            name="shiftStart"
            value={formData.shiftStart}
            onChange={handleChange}
          />
        </label>

        <label>
          {t('employees.shiftFinishLabel')}: 
          <input
            type="time"
            name="shiftFinish"
            value={formData.shiftFinish}
            onChange={handleChange}
          />
        </label>

        <fieldset>
          <legend>{t('employees.workDaysLegend')}</legend> 
          {allDays.map(day => (
            <label key={day}>
              <input
                type="checkbox"
                name="workDays"
                value={day}
                checked={formData.workDays.includes(day)}
                onChange={handleWorkDaysChange}
              />
              {t(`days.${day.toLowerCase()}`)} 
            </label>
          ))}
        </fieldset>
        <br></br>
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
    </div>
  );
};

export default EditEmployeeForm;