import * as fs from 'fs';

import {
    Aborter, AnonymousCredential, BlockBlobURL, SharedKeyCredential, StorageURL,
} from '@azure/storage-blob';
import { Sight } from '@event/entity';
import { createImage, createSight, CreateSightInput } from '@event/func';
import { Azure, DB } from '@event/util';

import { createMockAccounts, init, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();

  done();
});

describe('sight-create', () => {

  it('should work', async () => {
    const input = new CreateSightInput();
    input.title = '天人合一';
    input.lat = 22.4215355;
    input.lng = 114.2076754;

    const output = await createSight(input, users.admin_1.id, users.admin_1.roles);

    expect(output).toBeDefined();

    const connection = await DB.getConnection();
    const sightRepository = connection.getRepository(Sight);

    const sight = await sightRepository.findOneOrFail(output.sightId);

    expect(sight.title).toBe(input.title);
    expect(sight.latLng).toBeDefined();
    expect(sight.latLng.latitude).toBe(input.lat);
    expect(sight.latLng.longitude).toBe(input.lng);
  });

  it('should work', async () => {
    const user = users.user_1;

    const createImageOutput = await createImage(user.id, user.roles);

    const buffer = fs.readFileSync('src/__test__/assets/l5VEsC2.png');

    // Upload
    const anonymousBlockBlobURL = new BlockBlobURL(
      createImageOutput.blobUrl,
      StorageURL.newPipeline(new AnonymousCredential()),
    );
    await anonymousBlockBlobURL.upload(Aborter.none, buffer, buffer.byteLength);

    // Create Sight
    const input = new CreateSightInput();
    input.title = '天人合一';
    input.lat = 22.4215355;
    input.lng = 114.2076754;
    input.imageIds = [createImageOutput.imageId];

    const output = await createSight(input, users.admin_1.id, users.admin_1.roles);

    expect(output).toBeDefined();

    const connection = await DB.getConnection();
    const sightRepository = connection.getRepository(Sight);

    const sight = await sightRepository.findOneOrFail(output.sightId);

    expect(sight.title).toBe(input.title);
    expect(sight.latLng).toBeDefined();
    expect(sight.latLng.latitude).toBe(input.lat);
    expect(sight.latLng.longitude).toBe(input.lng);

    // Delete
    const storageAccount = await Azure.getStorageAccount();
    const sharedKeyCredential = new SharedKeyCredential(storageAccount.name, storageAccount.key);
    const pipeline = StorageURL.newPipeline(sharedKeyCredential);

    const blockBlobURL = new BlockBlobURL(
      createImageOutput.blobUrl.substring(0, createImageOutput.blobUrl.indexOf('?')),
      pipeline,
    );
    await blockBlobURL.delete(Aborter.none);
  });

});
