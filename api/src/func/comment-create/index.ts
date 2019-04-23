import { IsDefined, IsInt, IsOptional, IsString, Length } from 'class-validator';

import { Comment, Role } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError } from '@event/util';

export class CreateCommentInput {

  @IsDefined()
  @IsString()
  @Length(2)
  content: string;

  @IsOptional()
  @IsInt()
  flightId?: number;

  @IsOptional()
  @IsInt()
  sightId?: number;

  @IsOptional()
  @IsInt()
  hotelId?: number;

}

export class CreateCommentOutput {
  constructor(public commentId: number) {
  }
}

export async function createComment(input: CreateCommentInput, userId?: number, roles?: Role[]): Promise<CreateCommentOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const commentRepository = connection.getRepository(Comment);

  const comment = new Comment();

  comment.content = input.content;
  comment.createUserId = userId;

  if (input.flightId !== undefined) {
    comment.flightId = input.flightId;
  } else if (input.sightId !== undefined) {
    comment.sightId = input.sightId;
  } else if (input.hotelId !== undefined) {
    comment.hotelId = input.hotelId;
  }

  const insertResult = await commentRepository.insert(comment);

  return new CreateCommentOutput(insertResult.identifiers[0].id);
}

export async function commentCreateFunc(context: any) {
  context.res = await Func.run1(
    context,
    createComment,
    CreateCommentInput,
    Authorized.permit({
    }),
  );
}
