import { hash } from 'bcryptjs';
import { IsDefined, IsEmail, IsInt, IsOptional, IsString, Length } from 'class-validator';

import { Role, Sex, User } from '@event/entity';
import { DB, Func, UnauthorizedError, UserFriendlyError } from '@event/util';

export class SignUpInput {
  @IsDefined()
  @IsEmail()
  emailAddress: string;

  @IsDefined()
  @Length(6)
  password: string;

  @IsOptional()
  @IsString()
  firstName?: string;

  @IsOptional()
  @IsString()
  lastName?: string;

  @IsOptional()
  @IsInt()
  sex?: Sex;
}

export async function signUp(input: SignUpInput) {
  const connection = await DB.getConnection();
  const userRepository = connection.getRepository(User);

  const users = await userRepository
    .createQueryBuilder()
    .where('emailAddress = :emailAddress', { emailAddress: input.emailAddress })
    .getMany();

  for (const user of users) {
    if (user.emailAddress === input.emailAddress) {
      throw new UserFriendlyError('The emailAddress is occupied.');
    }
  }

  const targetUser = new User();
  targetUser.emailAddress = input.emailAddress;
  targetUser.password = await hash(input.password, 12);
  targetUser.roles = [Role.Users];

  if (input.firstName !== undefined) {
    targetUser.firstName = input.firstName;
  }
  if (input.lastName !== undefined) {
    targetUser.lastName = input.lastName;
  }
  if (input.sex !== undefined && (input.sex in Sex) === false) {
    throw new UserFriendlyError('The input sex is invalid.');
  }

  await userRepository.insert(targetUser);
}

export async function authSignUpFunc(context: any) {
  context.res = await Func.run1(
    context,
    signUp,
    SignUpInput,
  );
}
