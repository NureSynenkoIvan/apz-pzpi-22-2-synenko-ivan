import React, { useState, useEffect } from 'react';
import QRCode from "react-qr-code";
import './ShiftQRCode.css';
import { useTranslation } from 'react-i18next';

function ShiftQRCode() {
  const { t } = useTranslation();
  const [qrCodeData, setQrCodeData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const endpoint = 'http://localhost:8080/shift';

  useEffect(() => {
    const fetchQRCode = async () => {
      const storedPhoneNumber = localStorage.getItem('userPhoneNumber');
      const storedPassword = localStorage.getItem('userPassword');

      if (!storedPhoneNumber || !storedPassword) {
        setError(t('shiftQrCode.authError'));
        setLoading(false);
        return;
      }

      const credentials = btoa(`${storedPhoneNumber}:${storedPassword}`);

      try {
        const response = await fetch(endpoint, {
          method: "GET",
          headers: {
            "Authorization": `Basic ${credentials}`
          }
        });

        if (response.ok) {
          const text = await response.text();
          setQrCodeData(text);
        } else {
          const errText = await response.text();
          setError(t('shiftQrCode.fetchError', { status: response.status, message: errText }));
        }
      } catch (err) {
        setError(t('shiftQrCode.networkError', { message: err.message }));
      } finally {
        setLoading(false);
      }
    };

    fetchQRCode();
  }, [t]);

  if (loading) {
    return <div className="qr-page"><p>{t('shiftQrCode.loading')}</p></div>;
  }

  if (error) {
    return <div className="qr-page error-state"><p>{error}</p></div>;
  }

  return (
    <div className="qr-page">
      <h2>{t('shiftQrCode.title')}</h2>
        <div style={{ height: "auto", margin: "0 auto", maxWidth: 256, width: "100%" }}>
          <QRCode
            size={1024}
            style={{ height: "auto", maxWidth: "100%", width: "100%" }}
            value={qrCodeData}
            viewBox={`0 0 1024 1024`}
          />
        </div>
        <p className="qr-text">{t('shiftQrCode.qrCodeValidity')}</p>
    </div>
  );
}

export default ShiftQRCode;