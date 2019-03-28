import { listSights } from '@event/func';

import { createMockAccounts, createMockSights, init, sights, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();
  await createMockSights();

  done();
});

describe('sight-list', () => {

  it('should work', async () => {
    const output = await listSights(users.admin_1.id, users.admin_1.roles);

    expect(output.sights).toBeDefined();
    expect(output.sights.length).toBe(Object.keys(sights).length);
  });

});
