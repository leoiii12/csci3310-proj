import { AccountStatus, checkAccount, CheckAccountInput } from '@event/func';

import { createMockAccounts, init, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();

  done();
});

describe('auth-check-account', () => {

  it('should work when emailAddress exists', async () => {
    const input = new CheckAccountInput();
    input.emailAddress = users.user_1.emailAddress;

    const output = await checkAccount(input);
    expect(output).toBeDefined();
    expect(output.accountStatus).toBe(AccountStatus.Exist);
  });

  it('should work when emailAddress does not exist', async () => {
    const input = new CheckAccountInput();
    input.emailAddress = 'zoom@zz.com';

    const output = await checkAccount(input);
    expect(output).toBeDefined();
    expect(output.accountStatus).toBe(AccountStatus.NotExist);
  });

});
