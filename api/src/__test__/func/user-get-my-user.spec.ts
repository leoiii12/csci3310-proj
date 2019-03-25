import { getMyUser } from '@event/func';

import { createMockAccounts, init, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();

  done();
});

describe('user-get-my-user', () => {

  it('should work', async () => {
    for (const u of Object.values(users)) {
      const output = await getMyUser(u.id, u.roles);

      expect(output.myUser).toBeDefined();
      expect(output.myUser.id).toBe(u.id);
      expect(output.myUser.emailAddress).toBe(u.emailAddress);
      expect(output.myUser.firstName).toBe(u.firstName);
      expect(output.myUser.lastName).toBe(u.lastName);
      expect(output.myUser.sex).toBe(u.sex);
      for (const r of u.roles) {
        expect(output.myUser.roles).toContain(r);
      }
    }
  });

});
