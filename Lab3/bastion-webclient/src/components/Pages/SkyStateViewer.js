import React, { useEffect, useState } from 'react';
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { MapContainer, TileLayer, Marker, Tooltip } from "react-leaflet";
import { useTranslation } from 'react-i18next';
import AlarmStatus from '../AlarmStatus';

const SkyStateViewer = () => {
  const { t } = useTranslation();
  const [allMarkers, setAllMarkers] = useState([]); 
  const [visibleDevices, setVisibleDevices] = useState({}); 
  const [distanceUnit, setDistanceUnit] = useState('km');
  const defendLat = parseFloat(process.env.REACT_APP_POINT_TO_DEFEND_LATITUDE);
  const defendLon = parseFloat(process.env.REACT_APP_POINT_TO_DEFEND_LONGITUDE);


  useEffect(() => {
    const fetchSkyState = async () => {
      try {
        const storedPhoneNumber = localStorage.getItem('userPhoneNumber');
        const storedPassword = localStorage.getItem('userPassword');
        const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);
        const backendUrl = process.env.REACT_APP_BACKEND_URL;


        const response = await fetch(backendUrl + '/radar-view', {
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

  function haversine(lat1, lon1, lat2, lon2, unit='km') {
    let rad 
    switch (unit) {
      case 'mi':
        rad = 3958.8; //Radius of Earth in miles
        break;
      case 'km':
      default: 
        rad = 6371 //Radius of Earth in km
        break;
    }

    let dLat = (lat2 - lat1) * Math.PI / 180.0;
        let dLon = (lon2 - lon1) * Math.PI / 180.0;
          
        lat1 = (lat1) * Math.PI / 180.0;
        lat2 = (lat2) * Math.PI / 180.0;
        
        let a = Math.pow(Math.sin(dLat / 2), 2) + 
                   Math.pow(Math.sin(dLon / 2), 2) * 
                   Math.cos(lat1) * 
                   Math.cos(lat2);
        let c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;

    }



  const greenIcon = new L.Icon({
    iconUrl: require('../../assets/marker-icon-green.png'),
    shadowUrl: require('../../assets/marker-shadow.png'),
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41],
    className: 'leaflet-green-icon' 
  });

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
      <MapContainer center={[defendLat, defendLon]} zoom={10} style={{ height: "400px", width: "80%" }}>
        <TileLayer
          attribution='&copy; <a href="https://osm.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {markersToDisplay.map(marker => (
          <Marker key={marker.id} position={[marker.lat, marker.lon]}>
            <Tooltip direction="top" offset={[0, -10]} opacity={1} permanent={false}>
              <div>
                <strong>{t('skyStateViewer.device')}:</strong> {marker.device}<br />
                <strong>{t('skyStateViewer.lat')}:</strong> {marker.lat.toFixed(5)}<br />
                <strong>{t('skyStateViewer.lon')}:</strong> {marker.lon.toFixed(5)}<br />
                <strong>{t('skyStateViewer.distance')}:</strong> {haversine(marker.lat, marker.lon, defendLat, defendLon, distanceUnit).toFixed(2)} {distanceUnit}<br />
              </div>
            </Tooltip>
          </Marker>
        ))}

        <Marker position={[defendLat, defendLon]} icon={greenIcon}>
          <Tooltip direction="top" offset={[0, -10]} opacity={1} permanent={false}>
            <div>
              <strong>{t('skyStateViewer.criticalPoint')}</strong><br />
              <strong>{t('skyStateViewer.lat')}:</strong> {defendLat}<br />
              <strong>{t('skyStateViewer.lon')}:</strong> {defendLon}<br />
            </div>
          </Tooltip>
        </Marker>
      </MapContainer>
    </div>
    <div style={{ margin: '10px', float: 'right' }}>
        <label>{t('skyStateViewer.unit')}:</label>{' '}
        <select value={distanceUnit} onChange={(e) => setDistanceUnit(e.target.value)}>
          <option value="km">{t('skyStateViewer.kilometers')}</option>
          <option value="mi">{t('skyStateViewer.miles')}</option>
        </select>
      </div>
    </>
  );
};

export default SkyStateViewer;