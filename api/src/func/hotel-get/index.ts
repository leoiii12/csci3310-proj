import { IsDefined, IsInt } from 'class-validator';

import { Comment, Hotel, HotelDto, Image, Rating, Role } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError, UserFriendlyError } from '@event/util';

export class GetHotelInput {

  @IsDefined()
  @IsInt()
  hotelId: number;

}

export class GetHotelOutput {
  constructor(public hotel: HotelDto) {
  }
}

export async function getHotel(input: GetHotelInput, userId?: number, roles?: Role[]): Promise<GetHotelOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const hotelRepository = connection.getRepository(Hotel);
  const ratingRepository = connection.getRepository(Rating);
  const commentRepository = connection.getRepository(Comment);
  const imageRepository = connection.getRepository(Image);

  const hotel = await hotelRepository.findOne({
    where: {
      id: input.hotelId,
    },
  });
  if (hotel === undefined) throw new UserFriendlyError('The hotel does not exist.');

  const ratings = await ratingRepository.find({
    where: {
      hotelId: input.hotelId,
    },
    relations: ['createUser'],
  });
  const comments = await commentRepository.find({
    where: {
      hotelId: input.hotelId,
    },
    relations: ['createUser'],
  });
  hotel.ratings = ratings;
  hotel.comments = comments;

  const images = await imageRepository.findByIds(hotel.imageIds);

  return new GetHotelOutput(HotelDto.from(hotel, images));
}

export async function hotelGetFunc(context: any) {
  context.res = await Func.run1(
    context,
    getHotel,
    GetHotelInput,
    Authorized.permit({
    }),
  );
}
