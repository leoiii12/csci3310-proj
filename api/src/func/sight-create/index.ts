import {
    IsDefined, IsInt, IsNumber, IsOptional, IsString, Length, ValidationError,
} from 'class-validator';

import {
    Aborter, BlobSASPermissions, BlobURL, BlockBlobURL, ContainerURL,
    generateBlobSASQueryParameters, SASProtocol, ServiceURL, SharedKeyCredential, StorageURL,
} from '@azure/storage-blob';
import { Image, Role, Sight } from '@event/entity';
import { Authorized, Azure, DB, Func, UnauthorizedError } from '@event/util';

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

  @IsOptional()
  @IsInt({ each: true })
  imageIds: number[];

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
  const imageRepository = connection.getRepository(Image);

  const sight = new Sight();

  sight.createUserId = userId;
  sight.title = input.title;
  sight.latLng = {
    latitude: input.lat,
    longitude: input.lng,
  };

  if (input.imageIds !== undefined && input.imageIds !== null) {
    const images = await imageRepository.findByIds(input.imageIds);
    if (input.imageIds.length !== images.length) {
      throw new ValidationError();
    }

    const storageAccount = await Azure.getStorageAccount();

    const sharedKeyCredential = new SharedKeyCredential(storageAccount.name, storageAccount.key);
    const pipeline = StorageURL.newPipeline(sharedKeyCredential);

    for (const image of images.filter(image => image.isPublic === false)) {
      // srcBlockBlobURL
      const srcBlockBlobURL = new BlockBlobURL(
        image.blobUrl,
        pipeline,
      );

      // containerURL
      const serviceURL = new ServiceURL(
        `https://${storageAccount.name}.blob.core.windows.net`,
        pipeline,
      );
      const containerName = 'images';
      const containerURL = ContainerURL.fromServiceURL(serviceURL, containerName);

      const blobName = image.id.toString();

      // destBlockBlobURL
      const blobURL = BlobURL.fromContainerURL(containerURL, blobName);
      const destBlockBlobURL = BlockBlobURL.fromBlobURL(blobURL);

      // Copy
      await destBlockBlobURL.startCopyFromURL(Aborter.none, srcBlockBlobURL.url.substring(0, srcBlockBlobURL.url.indexOf('?'))).catch(reason => console.log(reason));

      // blobSAS
      const now = new Date();
      now.setMinutes(now.getMinutes() - 5);
      const tmr = new Date();
      tmr.setFullYear(tmr.getFullYear() + 100);
      const blobSAS = generateBlobSASQueryParameters(
        {
          blobName,
          containerName,
          expiryTime: tmr,
          permissions: BlobSASPermissions.parse('r').toString(),
          protocol: SASProtocol.HTTPS,
          startTime: now,
          version: '2016-05-31',
        },
        sharedKeyCredential as SharedKeyCredential,
      );

      image.isPublic = true;
      image.blobUrl = `${blobURL.url}?${blobSAS}`;
    }

    await imageRepository.save(images);

    sight.imageIds = input.imageIds;
  }

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
