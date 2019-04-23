import {
    IsDefined, IsInt, IsNumber, IsOptional, IsString, Length, ValidationError,
} from 'class-validator';

import {
    Aborter, BlobSASPermissions, BlobURL, BlockBlobURL, ContainerURL,
    generateBlobSASQueryParameters, SASProtocol, ServiceURL, SharedKeyCredential, StorageURL,
} from '@azure/storage-blob';
import { Hotel, Image, Role } from '@event/entity';
import { Authorized, Azure, DB, Func, UnauthorizedError } from '@event/util';

export class CreateHotelInput {

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

export class CreateHotelOutput {
  constructor(public hotelId: number) {
  }
}

export async function createHotel(input: CreateHotelInput, userId?: number, roles?: Role[]): Promise<CreateHotelOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const hotelRepository = connection.getRepository(Hotel);
  const imageRepository = connection.getRepository(Image);

  const hotel = new Hotel();

  hotel.createUserId = userId;
  hotel.title = input.title;
  hotel.latLng = {
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

    hotel.imageIds = input.imageIds;
  }

  const insertResult = await hotelRepository.insert(hotel);

  return new CreateHotelOutput(insertResult.identifiers[0].id);
}

export async function hotelCreateFunc(context: any) {
  context.res = await Func.run1(
    context,
    createHotel,
    CreateHotelInput,
    Authorized.permit({
    }),
  );
}
