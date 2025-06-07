import React, { useState, useEffect } from 'react';
import './Employees.css'; 
import AddEmployeeForm from './AddEmployeeForm.js';
import EditEmployeeForm from './EditEmployeeForm.js';
import { useTranslation } from 'react-i18next';
import tzlookup from 'tz-lookup';
import { DateTime } from 'luxon';

const TIMEZONES = [
  "UTC",
  "Europe/Kyiv",
  "Europe/London",
  "Europe/Berlin",
  "America/New_York",
  "America/Los_Angeles",
  "Asia/Tokyo",
  "Asia/Shanghai",
  "Australia/Sydney"
];

function Employees() {
  const { t } = useTranslation();
  const [employeesData, setEmployeesData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isAddingEmployee, setIsAddingEmployee] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState(false);
  const [searchTerm , setSearchTerm] = useState('')
  const [selectedRole, setSelectedRole] = useState('ALL');
  const [showOnDutyOnly, setShowOnDutyOnly] = useState(false);
  const [sortOrderAsc, setSortOrderAsc] = useState(true);


  const defendLat = parseFloat(process.env.REACT_APP_POINT_TO_DEFEND_LATITUDE);
  const defendLon = parseFloat(process.env.REACT_APP_POINT_TO_DEFEND_LONGITUDE);
  const defendTimeZone = tzlookup(defendLat, defendLon);
  const [selectedTimeZone, setSelectedTimeZone] = useState(defendTimeZone)
  
  const role = localStorage.getItem('userRole');
  const backendUrl = process.env.REACT_APP_BACKEND_URL;
  const backendEndpoint = backendUrl + '/employees';

  useEffect(() => {
    const fetchEmployees = async () => {
      const storedPhoneNumber = localStorage.getItem('userPhoneNumber');
      const storedPassword = localStorage.getItem('userPassword');

      if (!storedPhoneNumber || !storedPassword) {
        setError(t('employees.authError'));
        setLoading(false);
        return;
      }

      const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);

      try {
        const response = await fetch(backendEndpoint + "/all", {
          method: "GET",
          headers: {
            "Authorization": `Basic ${credentials}`,
            "Content-Type": "application/json"
          }
        });

        if (response.ok) {
          const data = await response.json();
          setEmployeesData(data);
        } else {
          const errorData = await response.json().catch(() => response.text());
          setError(t('employees.loadError', { status: response.status, message: JSON.stringify(errorData) }));
        }
      } catch (err) {
        setError(t('employees.networkError', { message: err.message }));
        console.error('Fetch error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchEmployees();
  }, [t]);

  if (loading) {
    return (
      <div className="employees-page">
        <h2>{t('employees.loading')}</h2>
        <p>{t('employees.wait')}</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="employees-page error-state">
        <h2>{t('employees.error')}</h2>
        <p>{error}</p>
        <p>{t('employees.tryAgain')}</p>
      </div>
    );
  }
  const handleAdd = async (newEmployee) => {
    try {
      const storedPhoneNumber = localStorage.getItem('userPhoneNumber'); 
      const storedPassword = localStorage.getItem('userPassword');
      const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);
      
      

      const response = await fetch(backendEndpoint, {
        method: 'POST',
        headers: {
          'Authorization': `Basic ${credentials}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newEmployee),
      });

      if (response.ok) {
        setEmployeesData((prev) => [...prev, newEmployee]);
        alert(t('employees.addSuccess'));
      } else {
        const errorText = await response.text();
        alert(t('employees.addError', { message: errorText }));
      }
    } catch (err) {
      alert(t('employees.networkError', { message: err.message }));
    } finally {
      setIsAddingEmployee(false);
    }
  };

  const handleEdit = (employee) => {
    setEditingEmployee(employee);
  };

  const handleSave = async (updatedEmployee) => {
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
        body: JSON.stringify(updatedEmployee),
      });

      if (response.ok) {
        setEmployeesData(prevDevices =>
          prevDevices.map(emp =>
            updatedEmployee.phoneNumber === emp.phoneNumber ? updatedEmployee : emp
          )
        );
        alert(t('employees.updateSuccess'));
      } else {
        const errorText = await response.text();
        alert(t('employees.updateError', { message: errorText }));
      }
    } catch (err) {
      alert(t('employees.networkError', { message: err.message }));
    } finally {
      setEditingEmployee(false);
    }
  };

  const handleDelete = async (employee) => {
    if (!window.confirm(t('employees.confirmDelete', { firstName: employee.firstName, lastName: employee.lastName }))) return;

    try {
      const storedPhoneNumber = localStorage.getItem('userPhoneNumber'); 
      const storedPassword = localStorage.getItem('userPassword');
      const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);

      const response = await fetch(backendEndpoint + `/delete?phoneNumber=${employee.phoneNumber}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Basic ${credentials}`,
        }
      });

      if (response.ok) {
        setEmployeesData((prev) =>
          prev.filter((emp) => emp.phoneNumber !== employee.phoneNumber)
        );
        alert(t('employees.deleteSuccess'));
      } else {
        const errText = await response.text();
        alert(t('employees.deleteError', { message: errText }));
      }
    } catch (err) {
      alert(t('employees.networkError', { message: err.message }));
    }
  };

  const handleToggleDuty = async (employee) => {
    try {
      const storedPhoneNumber = localStorage.getItem('userPhoneNumber'); 
        const storedPassword = localStorage.getItem('userPassword');
        const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);
      const updatedEmployee = {
        ...employee,
        onDuty: !employee.onDuty,
      };
      

      await fetch(backendEndpoint + '/shift?phoneNumber='+employee.phoneNumber, {
        method: 'PUT',
        headers: {'Authorization': `Basic ${credentials}`, 
          'Content-Type': 'application/json' }
      });

      setEmployeesData((prev) =>
        prev.map((emp) => (emp.phoneNumber === employee.phoneNumber ? updatedEmployee : emp))
      );
    } catch (error) {
      console.error(t('employees.toggleDutyError'), error);
    }
  };

  const sortEmployeesByLastName = (list) => {
    return [...list].sort((a, b) => {
      if (a.lastName < b.lastName) return sortOrderAsc ? -1 : 1;
      if (a.lastName > b.lastName) return sortOrderAsc ? 1 : -1;
      return 0;
    });
  };

    const filteredEmployees = employeesData
    ? sortEmployeesByLastName((
        employeesData.filter(emp => {
            const matchesSearch = emp.lastName.toLowerCase().includes(searchTerm.toLowerCase());
            const matchesRole = selectedRole === 'ALL' || emp.role === selectedRole;
            const matchesOnDuty = showOnDutyOnly ? emp.onDuty : true; 
            return matchesSearch && matchesRole && matchesOnDuty; 
        })
      )) : [];

  const trimTime = (time) => {
    return DateTime.fromFormat(time, "HH:mm:ss", { zone: defendTimeZone })
    .setZone(selectedTimeZone)
    .toFormat("HH:mm")
  }

return (
    <div className="employees-page">
      <h2>{t('employees.infoTitle')}</h2>
      {role === 'ADMINISTRATOR' && !isAddingEmployee && (
            <button onClick={() => setIsAddingEmployee(true)}>{t('employees.addUserButton')}</button>
          )}

      {isAddingEmployee && (
          <AddEmployeeForm
            onSave={handleAdd}
            onCancel={() => setIsAddingEmployee(false)}
          />
        )}

        {editingEmployee && (
          <EditEmployeeForm
            employee={editingEmployee}
            onSave={handleSave}
            onCancel={() => setEditingEmployee(false)}
          />
        )}

        <div style={{ margin: '16px 0' }}>
          <label style={{ marginRight: '10px' }}>{t('employees.searchByLastName')}: </label>
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder={t('employees.searchPlaceholder')}
            style={{ padding: '6px', width: '200px', marginRight: '20px' }}
          />

          <label style={{ marginRight: '10px' }}>{t('employees.filterByRole')}: </label>
          <select
            value={selectedRole}
            onChange={(e) => setSelectedRole(e.target.value)}
            style={{ padding: '6px', marginRight: '20px' }}
          >
            <option value="ALL">{t('employees.allRoles')}</option>
            <option value="USER">{t('roles.user')}</option>
            <option value="DISPATCHER">{t('roles.dispatcher')}</option>
            <option value="ADMINISTRATOR">{t('roles.administrator')}</option>
          </select>

          <label>
            <input
              type="checkbox"
              checked={showOnDutyOnly}
              onChange={(e) => setShowOnDutyOnly(e.target.checked)}
              style={{ marginRight: '5px' }}
            />
            {t('employees.onDutyFilter')}
          </label>
          <button onClick={() => setSortOrderAsc(!sortOrderAsc)} style={{ marginBottom: '10px', marginLeft: '10px' }}>
            {sortOrderAsc ? t('employees.sortAZ') : t('employees.sortZA')}
          </button>
          <br />
          <div style={{ margin: '16px 0' }}>
            <label style={{ marginRight: '10px' }}>{t('employees.selectTimeZone')}:</label>
            <select
              value={selectedTimeZone}
              onChange={(e) => setSelectedTimeZone(e.target.value)}
              style={{ padding: '6px', marginRight: '20px' }}
            >
              {TIMEZONES.map((zone) => (
                <option key={zone} value={zone}>
                  {zone}
                </option>
              ))}
            </select>
          </div>
        </div>

      {employeesData && employeesData.length > 0 ? (
        <table className="employee-table">
          <thead>
            <tr>
              <th>{t('employees.firstName')}</th>
              <th>{t('employees.lastName')}</th>
              <th>{t('employees.phoneNumber')}</th>
              <th>{t('employees.position')}</th>
              <th>{t('employees.role')}</th>
              <th>{t('employees.workDaysHeader')}</th>
              <th>{t('employees.scheduleHeader')} {selectedTimeZone}</th>
              <th>{t('employees.onDutyHeader')}</th>
              <th>{t('employees.actions')}</th>
            </tr>
          </thead>
          <tbody>
            {
            filteredEmployees.map((emp, index) => (
              <tr key={index}>
                <td>{emp.firstName}</td>
                <td>{emp.lastName}</td>
                <td>{emp.phoneNumber}</td>
                <td>{emp.position}</td>
                <td>{t(`roles.${emp.role.toLowerCase()}`)}</td>
                <td>{emp.workTime?.workDays?.map(day => t(`days.${day.toLowerCase()}`)).join(', ') || 'N/A'}</td>
                <td>
                  {emp.workTime
                    ? `${trimTime(emp.workTime.shiftStart)} – ${trimTime(emp.workTime.shiftFinish)}`
                    : t('employees.notAvailable')}
                </td>                
                <td>{emp.onDuty ? t('devices.yes') : t('devices.no')}</td>
                <td>
                  <button
                    onClick={() => handleToggleDuty(emp)}
                    style={{ padding: '4px 8px', fontSize: '0.9rem' }}
                  >
                    {emp.onDuty ? t('employees.toggleOffDuty') : t('employees.toggleOnDuty')}
                  </button>
                </td>
                {role === 'ADMINISTRATOR' && (
                  <td>
                    <button onClick={() => handleEdit(emp)}>{t('devices.edit')}</button>{' '}
                    <button onClick={() => handleDelete(emp)}>{t('devices.delete')}</button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>{t('employees.noData')}</p>
      )}
    </div>
  );
}

export default Employees;