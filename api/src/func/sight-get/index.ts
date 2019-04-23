import { IsDefined, IsInt } from 'class-validator';

import { Comment, Image, Rating, Role, Sight, SightDto } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError, UserFriendlyError } from '@event/util';

export class GetSightInput {

  @IsDefined()
  @IsInt()
  sightId: number;

}

export class GetSightOutput {
  constructor(public sight: SightDto) {
  }
}

export async function getSight(input: GetSightInput, userId?: number, roles?: Role[]): Promise<GetSightOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const sightRepository = connection.getRepository(Sight);
  const ratingRepository = connection.getRepository(Rating);
  const commentRepository = connection.getRepository(Comment);
  const imageRepository = connection.getRepository(Image);

  const sight = await sightRepository.findOne({
    where: {
      id: input.sightId,
    },
  });
  if (sight === undefined) throw new UserFriendlyError('The sight does not exist.');

  const ratings = await ratingRepository.find({
    where: {
      sightId: input.sightId,
    },
    relations: ['createUser'],
  });
  const comments = await commentRepository.find({
    where: {
      sightId: input.sightId,
    },
    relations: ['createUser'],
  });
  sight.ratings = ratings;
  sight.comments = comments;

  const images = await imageRepository.findByIds(sight.imageIds);

  return new GetSightOutput(SightDto.from(sight, images));
}

export async function sightGetFunc(context: any) {
  context.res = await Func.run1(
    context,
    getSight,
    GetSightInput,
    Authorized.permit({
    }),
  );
}
