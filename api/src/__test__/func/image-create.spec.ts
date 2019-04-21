import {
    Aborter, AnonymousCredential, BlockBlobURL, SharedKeyCredential, StorageURL,
} from '@azure/storage-blob';
import { createImage } from '@event/func';
import { Azure } from '@event/util';

import { createMockAccounts, createMockSights, init, users } from '../util/mock';

beforeAll(async (done) => {
  await init();
  await createMockAccounts();
  await createMockSights();

  done();
});

describe('image-create', () => {

  it('should work', async () => {
    const user = users.user_1;

    const output = await createImage(user.id, user.roles);

    expect(output).toBeDefined();

    // Upload
    const anonymousBlockBlobURL = new BlockBlobURL(
      output.blobUrl,
      StorageURL.newPipeline(new AnonymousCredential()),
    );
    await anonymousBlockBlobURL.upload(Aborter.none, '123', 3);

    // Delete
    const storageAccount = await Azure.getStorageAccount();
    const sharedKeyCredential = new SharedKeyCredential(storageAccount.name, storageAccount.key);
    const pipeline = StorageURL.newPipeline(sharedKeyCredential);

    const blockBlobURL = new BlockBlobURL(
      output.blobUrl.substring(0, output.blobUrl.indexOf('?')),
      pipeline,
    );
    await blockBlobURL.delete(Aborter.none);
  });

});
