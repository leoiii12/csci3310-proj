import { Rating } from '@event/entity';
import { createRating, CreateRatingInput } from '@event/func';
import { DB } from '@event/util';

import { createMockAccounts, createMockSights, init, sights, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();
  await createMockSights();

  done();
});

describe('rating-create', () => {

  it('should work', async () => {
    const sight = sights.sight_1;
    const user = users.user_1;

    const input = new CreateRatingInput();
    input.value = 5;
    input.sightId = sight.id;

    const output = await createRating(input, user.id, user.roles);

    expect(output).toBeDefined();

    const connection = await DB.getConnection();
    const ratingRepository = connection.getRepository(Rating);

    const rating = await ratingRepository.findOneOrFail(output.ratingId);

    expect(rating.value).toBe(input.value);
  });

  it('should work', async () => {
    const sight = sights.sight_1;
    const user = users.user_1;

    // Init
    let input = new CreateRatingInput();
    input.value = 1.1;
    input.sightId = sight.id;

    let output = await createRating(input, user.id, user.roles);

    expect(output).toBeDefined();

    const connection = await DB.getConnection();
    const ratingRepository = connection.getRepository(Rating);

    let rating = await ratingRepository.findOneOrFail(output.ratingId);

    expect(rating.value).toBe(input.value);

    // Update
    input = new CreateRatingInput();
    input.value = 3.2;
    input.sightId = sight.id;

    output = await createRating(input, user.id, user.roles);

    expect(output).toBeDefined();

    rating = await ratingRepository.findOneOrFail(output.ratingId);

    expect(rating.value).toBe(input.value);
  });

});
