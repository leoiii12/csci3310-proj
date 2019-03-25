import { compare } from 'bcryptjs';
import { IsDefined, Length, Validator } from 'class-validator';
import { sign } from 'jsonwebtoken';

import { User } from '@event/entity';
import { DB, Func, InternalServerError, UnauthorizedError, UserFriendlyError } from '@event/util';

export class AuthenticateInput {
  @IsDefined()
  @Length(5)
  idOrEmailAddress: string;

  @IsDefined()
  @Length(6)
  password: string;
}

export class AuthenticateOutput {
  constructor(public accessToken: string) {
  }
}

export async function authenticate(input: AuthenticateInput): Promise<AuthenticateOutput> {
  const secret = process.env.EVENT_AUTH_SECRET;
  if (secret === undefined) {
    throw new InternalServerError('EVENT_AUTH_SECRET undefined.');
  }

  const connection = await DB.getConnection();
  const userRepository = connection.getRepository(User);

  const validator = new Validator();

  const users: User[] = [];
  if (validator.isEmail(input.idOrEmailAddress)) {
    const us = await userRepository.findOne({
      where: {
        emailAddress: input.idOrEmailAddress,
      },
    });

    if (us !== undefined) users.push(us);
  } else {
    const us = await userRepository.findOne({
      where: {
        id: input.idOrEmailAddress,
      },
    });

    if (us !== undefined) users.push(us);
  }

  const user = users.find(u =>
    u.id.toString() === input.idOrEmailAddress ||
    u.emailAddress === input.idOrEmailAddress,
  );
  if (user === undefined) {
    throw new UserFriendlyError('The user does not exist.');
  }

  const isValid = await compare(input.password, user.password);
  if (isValid === false) {
    throw new UnauthorizedError();
  }

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

  return new AuthenticateOutput(token);
}

export async function authAuthenticateFunc(context: any) {
  context.res = await Func.run1(
    context,
    authenticate,
    AuthenticateInput,
  );
}
