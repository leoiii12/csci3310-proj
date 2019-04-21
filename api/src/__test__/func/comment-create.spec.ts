import { Comment } from '@event/entity';
import { createComment, CreateCommentInput } from '@event/func';
import { DB } from '@event/util';

import { createMockAccounts, createMockSights, init, sights, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();
  await createMockSights();

  done();
});

describe('comment-create', () => {

  it('should work', async () => {
    const sight = sights.sight_1;
    const user = users.user_1;

    const input = new CreateCommentInput();
    input.content = 'Hello World.';
    input.sightId = sight.id;

    const output = await createComment(input, user.id, user.roles);

    expect(output).toBeDefined();

    const connection = await DB.getConnection();
    const commentRepository = connection.getRepository(Comment);

    const comment = await commentRepository.findOneOrFail(output.commentId);

    expect(comment.content).toBe(input.content);

  });

});
