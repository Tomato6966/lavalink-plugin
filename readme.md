<h1 align="center">
    kaogurai's lavalink plugin
</p>

<p align="center">
    <a href="https://github.com/kaogurai/lavalink-plugin/stargazers">
        <img src="https://img.shields.io/github/stars/kaogurai/lavalink-plugin?style=social">
    </a>
    <a href="https://github.com/kaogurai/lavalink-plugin/blob/main/LICENSE">
        <img src="https://img.shields.io/github/license/kaogurai/lavalink-plugin">
    </a>
</p>

## Requirements
You must host an instance of [deezer-proxy.](https://github.com/ryan5453/deezer-proxy) It requires a Redis instance to work, so it's usually easiest to use the provided docker-compose file.

# Configuration
The plugin is configured using the root-level `plugin` key in the application.yml file. 

```yaml
plugins:
  kaogurai:
    deezer: # Custom providers for track loading. This is the default
      proxyURL: http://localhost:9999 # URL of the deezer-proxy instance - do not use a trailing slash
      authKey: "" # Optional auth key for deezer-proxy - Right now this is NOT implemented, so you must not set an auth key on the deezer-proxy instance. Make sure you configure your firewall correctly to prevent unauthorized access to the proxy.
```