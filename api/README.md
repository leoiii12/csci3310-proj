# csci3310-proj

Azure Functions + MSSQL + VSCode

# Useful commands
```bash
npm run typeorm:cli -- migration:generate -c dev -n 'Init'
npm run typeorm:cli -- migration:run -c dev
npm run typeorm:cli -- migration:revert -c dev
npm run build:dev
npm run build:prod
npm run clean
npm run test
```

# Development
You need to set up a local mssql instance, and a local azure functions runtime.

## Install VSCode
https://go.microsoft.com/fwlink/?LinkID=620882

## Install nodejs
https://nodejs.org/en/download/ This package will install Node.js & npm

## Azure functions runtime
https://github.com/Azure/azure-functions-core-tools

## Install dotnet
https://dotnet.microsoft.com/download

To run the project locally, press F5 in VSCode.
Details available in https://code.visualstudio.com/tutorials/functions-extension/run-app

## MSSQL
```shell
docker run --name mssql -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=yourStrong(!)Password' -p 1433:1433 -d microsoft/mssql-server-linux:2017-latest
docker exec -it mssql /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'yourStrong(!)Password'
```

```sql
CREATE DATABASE [event331];
GO
```

## Database migration, configs on ormconfig.json
```shell
npm run migrations:generate -- '{MIGRATION_NAME}'
npm run migrations:run
```

1. Modify codes in src/entity
2. Generate a migration
3. Apply all migrations
