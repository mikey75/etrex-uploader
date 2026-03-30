# StatsHunters Explorer Tiles Integration

Application has explorer tiles support now via the StatsHunters API.

If you want to use StatsHunters integration you need to:

- have a StatsHunters account, and probably some tracks uploaded there or synced from Strava :)

 
- add the following to `config.properties`:
```properties
statshunters.url=https\://www.statshunters.com/api/<YOUR_API_KEY>/tiles
```

where `YOUR_API_KEY` is your StatsHunters key. You can get it from your StatsHunters account settings.

Now start/restart the app.

If everything is ok, you should see the unselected StatsHunters enabler checkbox on the top left of the map.
If you want to see your explorer tiles, just click it :)