import { hash } from 'bcryptjs';
import { IsDefined, IsEmail } from 'class-validator';

import { Role, User } from '@event/entity';
import { DB, Func, UnauthorizedError } from '@event/util';

export class InitInput {
  @IsDefined()
  @IsEmail()
  emailAddress: string;

  @IsDefined()
  password: string;
}

export async function init(input: InitInput) {
  const connection = await DB.getConnection();
  const userRepository = connection.getRepository(User);

  if (await userRepository.count() > 0) {
    throw new UnauthorizedError();
  }

  const newUser = new User();

  newUser.id = Math.ceil(Math.random() * 10000) + 10000;
  newUser.emailAddress = input.emailAddress;
  newUser.password = await hash(input.password, 12);
  newUser.roles = [Role.Admins];

  await userRepository.insert(newUser);
}

export async function initFunc(context: any) {
  context.res = await Func.run1(
    context,
    init,
    InitInput,
  );
}
