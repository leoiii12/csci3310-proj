import { Sight } from '@event/entity';
import { createSight, CreateSightInput } from '@event/func';
import { DB } from '@event/util';

import { createMockAccounts, init, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();

  done();
});

describe('sight-create', () => {

  it('should work', async () => {
    const input = new CreateSightInput();
    input.title = '天人合一';
    input.lat = 22.4215355;
    input.lng = 114.2076754;

    const output = await createSight(input, users.admin_1.id, users.admin_1.roles);

    expect(output).toBeDefined();

    const connection = await DB.getConnection();
    const sightRepository = connection.getRepository(Sight);

    const sight = await sightRepository.findOneOrFail(output.sightId);

    expect(sight.title).toBe(input.title);
    expect(sight.latLng).toBeDefined();
    expect(sight.latLng.latitude).toBe(input.lat);
    expect(sight.latLng.longitude).toBe(input.lng);
  });

});
