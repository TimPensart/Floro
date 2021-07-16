# Floro app
App showing the available bikes at BlueBike stations &amp; Parking spaces in the city of Ghent
<div style="display: flex">
	<kbd><img alt="screenshot" src="https://github.com/TimPensart/MobiliteitCaseGent/blob/main/Screenshot_20210715-173336.jpg" width="250" ></kbd>
	<kbd><img alt="screenshot" src="https://github.com/TimPensart/MobiliteitCaseGent/blob/main/Screenshot_20210716-004140.jpg" width="250" ></kbd>
</div>

## Made with Android Studio
This project was made for a case provided by In The Pocket.
	
## Setup
To run this project, install it locally using npm:

```
$ cd ../MobiliteitCaseGent
$ npm install
$ npm start
``` 
## API usage
<img alt="screenshot"  src="http://appsforghent.be/themes/appsforghent/assets/png/gent2.png" width="35" > [Open Data Portaal](https://data.stad.gent/explore/)
- [Blue Bike Gent Dampoort](https://data.stad.gent/explore/dataset/blue-bike-deelfietsen-gent-dampoort/api/)
- [Blue Bike Gent Sint-Pieters (M. Hendrikaplein)](https://data.stad.gent/explore/dataset/blue-bike-deelfietsen-gent-sint-pieters-m-hendrikaplein/api/)
- [Blue Bike Gent Sint-Pieters (St. Denijslaan)](https://data.stad.gent/explore/dataset/blue-bike-deelfietsen-gent-sint-pieters-st-denijslaan/api/)
- [Real time occupation of parking garages Gent](https://data.stad.gent/explore/dataset/bezetting-parkeergarages-real-time/api/)

## Packages info

- **expo-clipboard** was used for copying to clipboard functionality
</br>https://docs.expo.io/versions/latest/react-native/clipboard/
- **react-native-map-link** was used for showing locations in a maps app (e.g. google maps) using latitude & longitude
</br>https://github.com/flexible-agency/react-native-map-link

## Tested on
- Android emulator: **Pixel 3XL with API level 30**
- Fysical android device: **OnePlus 6 with API level 28**

## References
- [react-native (0.63.2)](https://github.com/facebook/react-native)
- [react-native-elements](https://reactnativeelements.com/)
- [expo](https://docs.expo.io/)
- [typescript](https://reactnative.dev/docs/typescript)
