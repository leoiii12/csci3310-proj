import { Hotel, HotelListDto, Image, Role } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError } from '@event/util';

export class ListHotelsOutput {
  constructor(public hotels: HotelListDto[]) {
  }
}

export async function listHotels(userId?: number, roles?: Role[]): Promise<ListHotelsOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const hotelRepository = connection.getRepository(Hotel);
  const imageRepository = connection.getRepository(Image);

  const hotels = await hotelRepository.find();

  const images = await imageRepository.findByIds([...new Set(hotels.reduce((pv, cv) => pv.concat(cv.imageIds), [] as number[]))]);
  const imagesDict = images.reduce((pv, cv) => { pv[cv.id] = cv; return pv; }, {} as { [id: number]: Image });

  return new ListHotelsOutput(hotels.map(s => HotelListDto.from(s, imagesDict)));
}

export async function hotelListFunc(context: any) {
  context.res = await Func.run0(
    context,
    listHotels,
    Authorized.permit({
    }),
  );
}
