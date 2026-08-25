> [!WARNING]
> **This project is deprecated and no longer maintained.**
> It was built as a student project and is archived here for reference only.
> It receives no updates, no bug fixes and no security patches. The Plant.id API
> key it needs is not included in this repository. Do not use it in production.

<img alt="screenshot" src="https://github.com/TimPensart/Floro/blob/master/Images/Floro%20logo%404x.png" height="80" />

# Floro android app
Floro is search and learn game made for youth to discover the richness and diversity of plants in nature.

<kbd><img alt="screenshot" src="https://www.timpensart.be/static/media/floro-mockup.8c78581a.png" height="350" /></kbd>

## Android open testing (live)
https://play.google.com/store/apps/details?id=com.timpensart.floro

<img alt="screenshot" src="https://github.com/TimPensart/Floro/blob/master/Images/QR.png" height="200" />

## Figma Clickable Designs
https://www.figma.com/proto/ciOHe0geX4AAlNHQsK3Snl/Floro-Master-Prototype?page-id=0%3A1&node-id=108%3A2&viewport=1152%2C134%2C0.06751421093940735&scaling=min-zoom&starting-point-node-id=108%3A2

## API technology
When the user makes a picture of a plant, it gets sent to <img alt="screenshot"  src="https://plant.id/assets/logo.large.png" width="35" > [Plant.id API](https://plant.id/) that responds with a list containing suggestions of plants.
Floro filters the ones with common names and highest % probability.

### Building it yourself
The API key is not in this repository. To build, create
`app/src/main/res/values/apikey.xml` (git-ignored) with your own Plant.id key:

```xml
<resources>
    <string name="api_key">YOUR_PLANT_ID_API_KEY</string>
</resources>
```

Note that any key shipped inside an Android APK is extractable by anyone who
downloads the app. This project does it the naive way; a real app should proxy
the call through a backend that holds the key.

*Design and development by Tim Pensart*
