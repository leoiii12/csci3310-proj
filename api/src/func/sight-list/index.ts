import { Role, Sight, SightListDto } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError } from '@event/util';

export class ListSightsOutput {
  constructor(public sights: SightListDto[]) {
  }
}

export async function listSights(userId?: number, roles?: Role[]): Promise<ListSightsOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const sightRepository = connection.getRepository(Sight);

  const sights = await sightRepository.find();

  return new ListSightsOutput(sights.map(s => SightListDto.from(s)));
}

export async function sightListFunc(context: any) {
  context.res = await Func.run0(
    context,
    listSights,
    Authorized.permit({
    }),
  );
}
