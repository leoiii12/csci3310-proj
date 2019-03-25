import { verify } from 'jsonwebtoken';

import { User } from '@event/entity';
import { authenticate, AuthenticateInput } from '@event/func';
import { DB, UnauthorizedError, UserFriendlyError } from '@event/util';

import { createMockAccounts, init, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();

  done();
});

describe('auth-authenticate', () => {

  it('should work when correct password with id', async () => {
    const targetUser = users.user_1;

    const input = new AuthenticateInput();
    input.idOrEmailAddress = targetUser.id.toString();
    input.password = targetUser.password;

    const output = await authenticate(input);
    expect(output).toBeDefined();

    const connection = await DB.getConnection();
    const userRepository = connection.getRepository(User);

    const count = await userRepository.count();
    expect(count).toBe(Object.keys(users).length);

    const decoded = await verify(output.accessToken, process.env.EVENT_AUTH_SECRET as string) as any;
    expect(decoded.sub).toBe(targetUser.id.toString());
  });

  it('should work when correct password with emailAddress', async () => {
    const targetUser = users.user_1;

    const input = new AuthenticateInput();
    input.idOrEmailAddress = targetUser.emailAddress;
    input.password = targetUser.password;

    const output = await authenticate(input);
    expect(output).toBeDefined();

    const connection = await DB.getConnection();
    const userRepository = connection.getRepository(User);

    const count = await userRepository.count();
    expect(count).toBe(Object.keys(users).length);

    const decoded = await verify(output.accessToken, process.env.EVENT_AUTH_SECRET as string) as any;
    expect(decoded.sub).toBe(targetUser.id.toString());
  });

  it('should error when incorrect password', async () => {
    const targetUser = users.user_1;

    const input = new AuthenticateInput();
    input.idOrEmailAddress = targetUser.emailAddress;
    input.password = 'incorrect';

    await expect(authenticate(input)).rejects.toThrowError(UnauthorizedError);
  });

  it('should error when incorrect password', async () => {
    const input = new AuthenticateInput();
    input.idOrEmailAddress = users.user_1.emailAddress;

    // tslint:disable-next-line:max-line-length
    input.password = 'incorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrectincorrect';

    await expect(authenticate(input)).rejects.toThrowError(UnauthorizedError);
  });

  it('should error when no sign up', async () => {
    const input = new AuthenticateInput();
    input.idOrEmailAddress = 'A000000';
    input.password = 'incorrect';

    await expect(authenticate(input)).rejects.toThrowError(UserFriendlyError);
  });

  it('should not work when empty input', async () => {
    const input = new AuthenticateInput();

    await expect(authenticate(input)).rejects.toThrowError(UserFriendlyError);
  });

});
