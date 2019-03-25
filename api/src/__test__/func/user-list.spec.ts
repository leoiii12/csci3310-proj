import { listUsers, ListUsersInput } from '@event/func';
import { UnauthorizedError } from '@event/util';

import { createMockAccounts, init, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();

  done();
});

describe('user-list', () => {

  it('should work', async () => {
    const input = new ListUsersInput();
    const output = await listUsers(input, users.admin_1.id, users.admin_1.roles);

    expect(output.users).toBeDefined();
    expect(output.users).toHaveLength(Object.values(users).length);
  });

  it('should not work when unauthorized', async () => {
    const input = new ListUsersInput();
    await expect(listUsers(input)).rejects.toThrowError(UnauthorizedError);
  });

});
