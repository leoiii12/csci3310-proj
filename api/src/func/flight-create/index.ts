import {
    IsDefined, IsInt, IsNumber, IsOptional, IsString, Length, ValidationError,
} from 'class-validator';

import {
    Aborter, BlobSASPermissions, BlobURL, BlockBlobURL, ContainerURL,
    generateBlobSASQueryParameters, SASProtocol, ServiceURL, SharedKeyCredential, StorageURL,
} from '@azure/storage-blob';
import { Flight, Image, Role } from '@event/entity';
import { Authorized, Azure, DB, Func, UnauthorizedError } from '@event/util';

export class CreateFlightInput {

  @IsDefined()
  @IsString()
  @Length(2)
  title: string;

  @IsOptional()
  @IsInt({ each: true })
  imageIds: number[];

}

export class CreateFlightOutput {
  constructor(public flightId: number) {
  }
}

export async function createFlight(input: CreateFlightInput, userId?: number, roles?: Role[]): Promise<CreateFlightOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const flightRepository = connection.getRepository(Flight);
  const imageRepository = connection.getRepository(Image);

  const flight = new Flight();

  flight.createUserId = userId;
  flight.title = input.title;

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

    flight.imageIds = input.imageIds;
  }

  const insertResult = await flightRepository.insert(flight);

  return new CreateFlightOutput(insertResult.identifiers[0].id);
}

export async function flightCreateFunc(context: any) {
  context.res = await Func.run1(
    context,
    createFlight,
    CreateFlightInput,
    Authorized.permit({
    }),
  );
}
