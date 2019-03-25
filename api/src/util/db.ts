import { Connection, createConnection, getConnectionOptions } from 'typeorm';

import { ENTITIES } from '@event/entity';

export namespace DB {

  let connection: Connection;

  export async function getConnection(): Promise<Connection> {
    if (connection) {
      return connection;
    }

    const connectionOptions = await getConnectionOptions(process.env.DB_PROFILE || 'dev');

    Object.assign(connectionOptions, {
      entities: ENTITIES,
    });

    connection = await createConnection(connectionOptions);

    return connection;
  }

}
