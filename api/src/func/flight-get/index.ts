import { IsDefined, IsInt } from 'class-validator';

import { Comment, Flight, FlightDto, Image, Rating, Role } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError, UserFriendlyError } from '@event/util';

export class GetFlightInput {

  @IsDefined()
  @IsInt()
  flightId: number;

}

export class GetFlightOutput {
  constructor(public flight: FlightDto) {
  }
}

export async function getFlight(input: GetFlightInput, userId?: number, roles?: Role[]): Promise<GetFlightOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const flightRepository = connection.getRepository(Flight);
  const ratingRepository = connection.getRepository(Rating);
  const commentRepository = connection.getRepository(Comment);
  const imageRepository = connection.getRepository(Image);

  const flight = await flightRepository.findOne({
    where: {
      id: input.flightId,
    },
  });
  if (flight === undefined) throw new UserFriendlyError('The flight does not exist.');

  const ratings = await ratingRepository.find({
    where: {
      flightId: input.flightId,
    },
    relations: ['createUser'],
  });
  const comments = await commentRepository.find({
    where: {
      flightId: input.flightId,
    },
    relations: ['createUser'],
  });
  flight.ratings = ratings;
  flight.comments = comments;

  const images = await imageRepository.findByIds(flight.imageIds);

  return new GetFlightOutput(FlightDto.from(flight, images));
}

export async function flightGetFunc(context: any) {
  context.res = await Func.run1(
    context,
    getFlight,
    GetFlightInput,
    Authorized.permit({
    }),
  );
}
