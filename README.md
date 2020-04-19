# Embarker
## Setup project repository
1. Clone the repository
2. Download MariaDB and set up a user with access to a two local databases, one for running JUnit tests and one for running the plugin on the server
   * Downloading it with [Homebrew](https://formulae.brew.sh/formula/mariadb) is recommended
3. Configure `src/main/resources/config.yml` in the repository with the JUnit testing database information to run tests
4. Build the plugin with `./gradlew build` to run tests and generate `build/libs/Embarker-all.jar`

## Run plugin on a server
1. Download Minecraft server software [Paper](https://papermc.io/) and set it up locally
2. Copy the built `Embarker-all.jar` plugin in `build/libs/Embarker-all.jar` into the server's `/plugins` directory
3. Create a `/plugins/Embarker/config.yml` file and set it up with your database information
4. Start Minecraft server
