import { IsDefined, IsDivisibleBy, IsInt, IsOptional, IsString, Length } from 'class-validator';
import { Brackets } from 'typeorm';

import { Role, User, UserDto } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError } from '@event/util';

export class ListUsersInput {
  @IsDefined()
  @IsInt()
  @IsDivisibleBy(20)
  skip: number;

  @IsOptional()
  @IsString()
  @Length(2)
  name?: string;

  @IsOptional()
  @IsInt()
  sex?: number;
}

export class ListUsersOutput {
  constructor(public users: UserDto[]) {
  }
}

export async function listUsers(input: ListUsersInput, userId?: number, roles?: Role[]): Promise<ListUsersOutput> {
  if (userId === undefined) {
    throw new UnauthorizedError();
  }
  if (roles === undefined) {
    throw new UnauthorizedError();
  }

  const connection = await DB.getConnection();
  const userRepository = connection.getRepository(User);

  let usersQuery = userRepository.createQueryBuilder('user');

  // Name Filter
  if (input.name !== undefined) {
    usersQuery = usersQuery.andWhere(new Brackets((qb) => {
      qb.orWhere('user.firstName LIKE :name', { name: `${input.name}%` });
      qb.orWhere('user.lastName LIKE :name', { name: `${input.name}%` });
    }));
  }

  // Sex Filter
  if (input.sex !== undefined) {
    usersQuery = usersQuery.andWhere('user.sex = :sex', { sex: input.sex });
  }

  const users = await usersQuery
    .orderBy('user.id')
    .skip(input.skip)
    .take(20)
    .getMany();

  return new ListUsersOutput(users.map(g => UserDto.from(g)));
}

export async function userListFunc(context: any) {
  context.res = await Func.run1(
    context,
    listUsers,
    ListUsersInput,
    Authorized.permit({
      anyRoles: [Role.Admins],
    }),
  );
}
