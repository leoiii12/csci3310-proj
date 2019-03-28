import { getSight, GetSightInput } from '@event/func';

import { createMockAccounts, createMockSights, init, sights, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();
  await createMockSights();

  done();
});

describe('sight-get', () => {

  it('should work', async () => {
    for (const sight of Object.values(sights)) {
      const input = new GetSightInput();
      input.sightId = sight.id;

      const output = await getSight(input, users.admin_1.id, users.admin_1.roles);

      expect(output.sight).toBeDefined();
      expect(output.sight.title).toBe(sight.title);
    }
  });

});
