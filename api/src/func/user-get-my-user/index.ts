import { MyUserDto, Role, User } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError } from '@event/util';

export class GetMyUserOutput {
  constructor(public myUser: MyUserDto) {
  }
}

export async function getMyUser(userId?: number, roles?: Role[]): Promise<GetMyUserOutput> {
  if (userId === undefined) {
    throw new UnauthorizedError();
  }

  const connection = await DB.getConnection();
  const userRepository = connection.getRepository(User);

  const myUser = await userRepository.findOneOrFail(userId);

  return new GetMyUserOutput(MyUserDto.from(myUser));
}

export async function userGetMyUserFunc(context: any) {
  context.res = await Func.run0(
    context,
    getMyUser,
    Authorized.permit({
    }));
}
