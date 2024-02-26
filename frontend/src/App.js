import React, { useState, useEffect } from 'react';
import { Grid, Paper, Typography } from '@material-ui/core';

import stores from './StoreCoordinates';


function formatTimestamp(timeNow, timestampInSeconds) {
  const timestampInMilliseconds = timestampInSeconds * 1000;
  const timeDifferenceInMilliseconds = timeNow - timestampInMilliseconds;
  const hoursDifference = Math.floor(timeDifferenceInMilliseconds / (60 * 60 * 1000));
  const minutesDifference = Math.floor((timeDifferenceInMilliseconds % (60 * 60 * 1000)) / (60 * 1000));
  return `${hoursDifference} hrs ${minutesDifference} min ago`;
}

function calculateDistance(c1, c2) {
  const toRad = (degrees) => degrees * (Math.PI / 180);

  const [lat1, lon1] = c1.map(parseFloat);
  const [lat2, lon2] = c2.map(parseFloat);

  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a = Math.sin(dLat / 2) ** 2 + Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLon / 2) ** 2;
  const earthRadiusInMiles = 3959;
  return Math.round(earthRadiusInMiles * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
}

const App = () => {
  let [data, setData] = useState([]);
  const [location, setLocation] = useState(false);
  let timeNow = new Date().getTime();


  useEffect(() => {

      if ('geolocation' in navigator) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
      if (position && position.coords && position.coords.latitude != null) {
              setLocation({
                latitude: position.coords.latitude,
                longitude: position.coords.longitude,
              });
            }}
        )
      }

    const fetchData = async () => {
      try {
        const response = await fetch('http://service.omgdeals.net/frontpage?hrs=220');
        const jsonData = await response.json();
        console.log(jsonData);
        setData(jsonData);
      } catch (error) {
        console.error('Error fetching data:', error);
        throw new Error('URL NOT SETT', error);
      }
    };

    fetchData();
  }, []);

  data.sort((a, b) => b.timestamp - a.timestamp);

  return (
      <Grid container spacing={2}>
        {data.map((item) => (
          <Grid item key={item.upc_store_retailer} xs={12} sm={6} md={3}>
            <Paper elevation={3} style={{ padding: 16 }}>

            <Typography style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography>  <img src={`${item.image}?odnHeight=100&odnWidth=100`}/></Typography>
              <span><Typography variant="subtitle2" align="right">{formatTimestamp(timeNow, item.timestamp)}</Typography></span>
               </Typography>
<Typography>{item.title.slice(0, 36)}</Typography>
              <Typography>Upc: {item.upc}</Typography>
              <Typography>Was: ${item.listPrice} Now: ${item.storePrice}</Typography>
<Typography style={{ display: 'flex', justifyContent: 'space-between' }}>
  <span>{item.retailer} #{item.store}</span>
  <span>{calculateDistance([location.latitude, location.longitude], [stores[item.store][0], stores[item.store][1]])} miles away</span>
</Typography>


            </Paper>
          </Grid>
        ))}
      </Grid>
  );
};

export default App;
