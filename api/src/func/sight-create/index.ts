import { IsDefined, IsNumber, IsOptional, IsString, Length } from 'class-validator';

import { Role, Sight } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError } from '@event/util';

export class CreateSightInput {

  @IsDefined()
  @IsString()
  @Length(2)
  title: string;

  @IsOptional()
  @IsNumber()
  lat?: number;

  @IsOptional()
  @IsNumber()
  lng?: number;

}

export class CreateSightOutput {
  constructor(public sightId: number) {
  }
}

export async function createSight(input: CreateSightInput, userId?: number, roles?: Role[]): Promise<CreateSightOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const sightRepository = connection.getRepository(Sight);

  const sight = new Sight();

  sight.createUserId = userId;
  sight.title = input.title;
  sight.latLng = {
    latitude: input.lat,
    longitude: input.lng,
  };

  const insertResult = await sightRepository.insert(sight);

  return new CreateSightOutput(insertResult.identifiers[0].id);
}

export async function sightCreateFunc(context: any) {
  context.res = await Func.run1(
    context,
    createSight,
    CreateSightInput,
    Authorized.permit({
    }),
  );
}
