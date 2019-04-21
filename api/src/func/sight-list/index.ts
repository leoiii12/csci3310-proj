import { Image, Role, Sight, SightListDto } from '@event/entity';
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
  const imageRepository = connection.getRepository(Image);

  const sights = await sightRepository.find();

  const images = await imageRepository.findByIds([...new Set(sights.reduce((pv, cv) => pv.concat(cv.imageIds), [] as number[]))]);
  const imagesDict = images.reduce((pv, cv) => { pv[cv.id] = cv; return pv; }, {} as { [id: number]: Image });

  return new ListSightsOutput(sights.map(s => SightListDto.from(s, imagesDict)));
}

export async function sightListFunc(context: any) {
  context.res = await Func.run0(
    context,
    listSights,
    Authorized.permit({
    }),
  );
}
