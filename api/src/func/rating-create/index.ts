import { IsDefined, IsInt, IsNumber, IsOptional, Max, Min, ValidationError } from 'class-validator';

import { Rating, Role } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError } from '@event/util';

export class CreateRatingInput {

  @IsDefined()
  @IsNumber()
  @Max(5)
  @Min(1)
  value: number;

  @IsOptional()
  @IsInt()
  flightId?: number;

  @IsOptional()
  @IsInt()
  sightId?: number;

  @IsOptional()
  @IsInt()
  transportId?: number;

}

export class CreateRatingOutput {
  constructor(public ratingId: number) {
  }
}

export async function createRating(input: CreateRatingInput, userId?: number, roles?: Role[]): Promise<CreateRatingOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();
  if (((input.flightId === undefined ? 0 : 1) + (input.sightId === undefined ? 0 : 1) + (input.transportId === undefined ? 0 : 1)) !== 1) throw new ValidationError();

  const connection = await DB.getConnection();
  const ratingRepository = connection.getRepository(Rating);

  let rating = await ratingRepository.findOne({
    where: {
      createUserId: userId,
      flightId: input.flightId === undefined ? null : input.flightId,
      sightId: input.sightId === undefined ? null : input.sightId,
      transportId: input.transportId === undefined ? null : input.transportId,
    },
  });

  if (rating === undefined) {
    rating = new Rating();

    rating.createUserId = userId;
    rating.flightId = input.flightId as number;
    rating.sightId = input.sightId as number;
    rating.transportId = input.transportId as number;
  }

  rating.value = input.value;

  await ratingRepository.save(rating);

  return new CreateRatingOutput(rating.id);
}

export async function ratingCreateFunc(context: any) {
  context.res = await Func.run1(
    context,
    createRating,
    CreateRatingInput,
    Authorized.permit({
    }),
  );
}
