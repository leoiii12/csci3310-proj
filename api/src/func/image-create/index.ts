import {
    Aborter, BlobSASPermissions, BlobURL, BlockBlobURL, ContainerURL,
    generateBlobSASQueryParameters, SASProtocol, ServiceURL, SharedKeyCredential, StorageURL,
} from '@azure/storage-blob';
import { Image, Role } from '@event/entity';
import { Authorized, Azure, DB, Func, UnauthorizedError } from '@event/util';

export class CreateImageOutput {
  constructor(public imageId: number, public blobUrl: string) {
  }
}

export async function createImage(userId?: number, roles?: Role[]): Promise<CreateImageOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const imageRepository = connection.getRepository(Image);

  const blobUrl = await getBlobUrl();

  const image = new Image();
  image.blobUrl = blobUrl;
  image.createUserId = userId;

  await imageRepository.save(image);

  return new CreateImageOutput(image.id, blobUrl);
}

async function getBlobUrl(): Promise<string> {
  const storageAccount = await Azure.getStorageAccount();

  const sharedKeyCredential = new SharedKeyCredential(storageAccount.name, storageAccount.key);
  const pipeline = StorageURL.newPipeline(sharedKeyCredential);

  const serviceURL = new ServiceURL(
    // When using AnonymousCredential, following url should include a valid SAS or support public access
    `https://${storageAccount.name}.blob.core.windows.net`,
    pipeline,
  );

  const containerName = 'images';
  const containerURL = ContainerURL.fromServiceURL(serviceURL, containerName);

  const blobName = getRandomAssetName();

  const blobURL = BlobURL.fromContainerURL(containerURL, blobName);
  const blockBlobURL = BlockBlobURL.fromBlobURL(blobURL);
  await blockBlobURL.upload(Aborter.none, '', 0);

  const now = new Date();
  now.setMinutes(now.getMinutes() - 5);

  const tmr = new Date();
  tmr.setDate(tmr.getDate() + 1);

  const blobSAS = generateBlobSASQueryParameters(
    {
      blobName,
      containerName,
      expiryTime: tmr,
      permissions: BlobSASPermissions.parse('w').toString(),
      protocol: SASProtocol.HTTPS,
      startTime: now,
      version: '2016-05-31',
    },
    sharedKeyCredential as SharedKeyCredential,
  );

  return `${blobURL.url}?${blobSAS}`;
}

function getRandomAssetName(): string {
  const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

  let text = '';
  for (let i = 0; i < 30; i = i + 1) {
    text += possible.charAt(Math.floor(Math.random() * possible.length));
  }

  return text;
}

export async function imageCreateFunc(context: any) {
  context.res = await Func.run0(
    context,
    createImage,
    Authorized.permit({
    }),
  );
}
