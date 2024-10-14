# GiftGalaxy

Laboratory of Advanced Programming project

## How to run the application

### Prerequisites

- Docker
- Docker compose

### Instructions

After cloning the repository:
1. Navigate to the `giftgalaxy` folder

2. Run the following commands to start the application:

    ```bash
    docker compose build
    docker compose up
    ```

3. To check if all the containers are running, use:

    ```bash
    docker ps
    ```

4. The application will be accessible at:

    [http://localhost:3000](http://localhost:3000)


If you need to stop the application, press `CTRL + C` in the terminal where `docker compose up` is running, or you can run:

```bash
docker compose down
```
