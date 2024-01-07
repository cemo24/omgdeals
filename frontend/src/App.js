import React, { useEffect, useState } from 'react';

const fetchData = async () => {
  try {
    const response = await fetch('https://randomuser.me/api');
    const data = await response.json();

    const firstName = data.results && data.results[0] && data.results[0].name.first;

    return firstName || 'No data found';
  } catch (error) {
    console.error('Error fetching data:', error);
    return 'Error fetching data';
  }
};

const MyComponent = () => {
  const [displayText, setDisplayText] = useState('...');

  useEffect(() => {
    let isMounted = true; 
    const fetchDataAndSetState = async () => {
      const text = await fetchData();
      if (isMounted) {
        setDisplayText(text);
      }
    };

    fetchDataAndSetState();

    return () => {
      isMounted = false;
    };
  }, []);
  return (
    <div>
      <h1>Making a Web Request</h1>
      <p>{displayText}</p>
    </div>
  );
};

export default MyComponent;
