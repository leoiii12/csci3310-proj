import { signUp, SignUpInput } from '@event/func';
import { UserFriendlyError } from '@event/util';

import { createMockAccounts, init, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();

  done();
});

describe('auth-sign-up', () => {

  it('should work', async () => {
    const input = new SignUpInput();
    input.emailAddress = getRandomEmailAddress();
    input.password = 'helloworld';

    await signUp(input);
  });

  it('should not work when duplicate', async () => {
    const user = users.user_1;

    const input = new SignUpInput();
    input.emailAddress = user.emailAddress;
    input.password = 'incorrect';

    await expect(signUp(input)).rejects.toThrowError(UserFriendlyError);
  });

  function getRandomEmailAddress() {
    let text = '';

    for (let i = 0; i < 8; i = i + 1) {
      text += Math.floor(Math.random() * 9);
    }

    return `${text}@event.hk`;
  }

});
