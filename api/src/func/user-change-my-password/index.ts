import { compare, hash } from 'bcryptjs';
import { IsDefined, Length } from 'class-validator';
import { sign } from 'jsonwebtoken';

import { Role, User } from '@event/entity';
import {
    Authorized, DB, Func, InternalServerError, UnauthorizedError, UserFriendlyError,
} from '@event/util';

export class ChangeMyPasswordInput {
  @IsDefined()
  @Length(6)
  oldPassword: string;

  @IsDefined()
  @Length(6)
  password: string;
}

export class ChangeMyPasswordOutput {
  constructor(public accessToken: string) {
  }
}

export async function changeMyPassword(input: ChangeMyPasswordInput, userId?: number, roles?: Role[]): Promise<ChangeMyPasswordOutput> {
  const secret = process.env.EVENT_AUTH_SECRET;
  if (secret === undefined) {
    throw new InternalServerError('EVENT_AUTH_SECRET undefined.');
  }

  if (userId === undefined) {
    throw new UnauthorizedError();
  }

  const connection = await DB.getConnection();
  const userRepository = connection.getRepository(User);

  const user = await userRepository.findOne(userId);
  if (user === undefined) {
    throw new UserFriendlyError('The user does not exist');
  }

  const isValid = await compare(input.oldPassword, user.password);
  if (isValid === false) {
    throw new UserFriendlyError('The old password is wrong');
  }
  user.password = await hash(input.password, 12);
  await userRepository.save(user);

  const options = {
    expiresIn: 60 * 60 * 24 * 31,
    audience: ['http://event331-api.azurewebsites.net'],
    issuer: 'http://event331-api.azurewebsites.net',
    subject: user.id.toString(),
  };

  const payload = {
    role: user.roles,
    gty: 'Auth/Authenticate',
  };

  const token = await sign(payload, secret, options);

  return new ChangeMyPasswordOutput(token);
}

export async function userChangeMyPasswordFunc(context: any) {
  context.res = await Func.run1(
    context,
    changeMyPassword,
    ChangeMyPasswordInput,
    Authorized.permit({
    }));
}
