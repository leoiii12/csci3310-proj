import { IsEmail } from 'class-validator';

import { User } from '@event/entity';
import { DB, Func } from '@event/util';

export class CheckAccountInput {
  @IsEmail()
  emailAddress: string;
}

export enum AccountStatus {
  NotExist = 0,
  Exist = 1,
}

export class CheckAccountOutput {
  constructor(public accountStatus: AccountStatus) {
  }
}

export async function checkAccount(input: CheckAccountInput): Promise<CheckAccountOutput> {
  const connection = await DB.getConnection();
  const userRepository = connection.getRepository(User);

  const count = await userRepository
    .createQueryBuilder()
    .select('COUNT(*)')
    .where('emailAddress = :emailAddress', { emailAddress: input.emailAddress })
    .getCount();

  if (count === 0) {
    return new CheckAccountOutput(AccountStatus.NotExist);
  }

  return new CheckAccountOutput(AccountStatus.Exist);
}

export async function authCheckAccountFunc(context: any) {
  context.res = await Func.run1(
    context,
    checkAccount,
    CheckAccountInput,
  );
}
