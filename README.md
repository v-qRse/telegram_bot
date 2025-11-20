src/main/resources/config.yaml
```yaml
token: "telegram-bot-token"
```

Start Redis DB: 
```shell
docker run --name redis -d -p 6379:6379 redis:8.0.0-alpine
```