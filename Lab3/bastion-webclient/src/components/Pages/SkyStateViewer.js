import React, { useEffect, useState } from 'react';
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { MapContainer, TileLayer, Marker } from "react-leaflet";
import { useTranslation } from 'react-i18next';
import AlarmStatus from '../AlarmStatus';

const SkyStateViewer = () => {
  const { t } = useTranslation();
  const [allMarkers, setAllMarkers] = useState([]); 
  const [visibleDevices, setVisibleDevices] = useState({}); 

  useEffect(() => {
    const fetchSkyState = async () => {
      try {
        const storedPhoneNumber = localStorage.getItem('userPhoneNumber');
        const storedPassword = localStorage.getItem('userPassword');
        const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);

        const response = await fetch('http://localhost:8080/radar-view', {
          method: "GET",
          headers: {
            "Authorization": `Basic ${credentials}`
          }
        });
        if (!response.ok) throw new Error(t('skyStateViewer.networkError'));
        const data = await response.json();

        const newMarkers = [];
        const newVisibleDevices = { ...visibleDevices };

        Object.entries(data.skyObjects || {}).forEach(([deviceName, objects]) => {
          if (newVisibleDevices[deviceName] === undefined) {
            newVisibleDevices[deviceName] = true;
          }

          objects.forEach((obj, index) => {
            const lat = obj.coordinates.latitude;
            const lon = obj.coordinates.longitude;
            if (lat && lon) {
              newMarkers.push({
                lat,
                lon,
                device: deviceName,
                id: `${deviceName}-${index}`
              });
            }
          });
        });
        setAllMarkers(newMarkers);
        setVisibleDevices(newVisibleDevices); 
      } catch (error) {
        console.error(t('skyStateViewer.fetchError'), error);
      }
    };

    fetchSkyState();

    const interval = setInterval(fetchSkyState, 1000);

    return () => clearInterval(interval);
  }, [t]); 

  delete L.Icon.Default.prototype._getIconUrl;
  L.Icon.Default.mergeOptions({
    iconRetinaUrl: require("leaflet/dist/images/marker-icon-2x.png"),
    iconUrl: require("leaflet/dist/images/marker-icon.png"),
    shadowUrl: require("leaflet/dist/images/marker-shadow.png")
  });

  const handleDeviceToggle = (deviceName) => {
    setVisibleDevices(prevVisibleDevices => ({
      ...prevVisibleDevices,
      [deviceName]: !prevVisibleDevices[deviceName] 
    }));
  };

  const markersToDisplay = allMarkers.filter(marker => visibleDevices[marker.device]);

  return (
    <>
    <div>
      <AlarmStatus></AlarmStatus>
    </div>
    <div style={{ display: 'flex' }}>
      <div style={{ width: '20%', padding: '10px', borderRight: '1px solid #ccc' }}>
        <h3>{t('skyStateViewer.devices')}</h3>
        {Object.keys(visibleDevices).length > 0 ? (
          <ul>
            {Object.keys(visibleDevices).map(deviceName => (
              <li key={deviceName} style={{ listStyleType: 'none', marginBottom: '5px' }}>
                <label>
                  <input
                    type="checkbox"
                    checked={visibleDevices[deviceName]}
                    onChange={() => handleDeviceToggle(deviceName)}
                    style={{ marginRight: '5px' }}
                  />
                  {deviceName}
                </label>
              </li>
            ))}
          </ul>
        ) : (
          <p>{t('skyStateViewer.noDevicesFound')}</p>
        )}
      </div>
      <MapContainer center={[49.92, 36.29]} zoom={10} style={{ height: "400px", width: "80%" }}>
        <TileLayer
          attribution='&copy; <a href="https://osm.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {markersToDisplay.map(marker => (
          <Marker key={marker.id} position={[marker.lat, marker.lon]}></Marker>
        ))}
      </MapContainer>
    </div>
    </>
  );
};

export default SkyStateViewer;