import { Flight, FlightListDto, Image, Role } from '@event/entity';
import { Authorized, DB, Func, UnauthorizedError } from '@event/util';

export class ListFlightsOutput {
  constructor(public flights: FlightListDto[]) {
  }
}

export async function listFlights(userId?: number, roles?: Role[]): Promise<ListFlightsOutput> {
  if (userId === undefined) throw new UnauthorizedError();
  if (roles === undefined) throw new UnauthorizedError();

  const connection = await DB.getConnection();
  const flightRepository = connection.getRepository(Flight);
  const imageRepository = connection.getRepository(Image);

  const flights = await flightRepository.find();

  const images = await imageRepository.findByIds([...new Set(flights.reduce((pv, cv) => pv.concat(cv.imageIds), [] as number[]))]);
  const imagesDict = images.reduce((pv, cv) => { pv[cv.id] = cv; return pv; }, {} as { [id: number]: Image });

  return new ListFlightsOutput(flights.map(s => FlightListDto.from(s, imagesDict)));
}

export async function flightListFunc(context: any) {
  context.res = await Func.run0(
    context,
    listFlights,
    Authorized.permit({
    }),
  );
}
