import { Comment } from './comment';
import { Flight } from './flight';
import { Rating } from './rating';
import { Sight } from './sight';
import { Transport } from './transport';
import { Role, Sex, User } from './user';

export * from './comment';
export * from './flight';
export * from './rating';
export * from './sight';
export * from './transport';
export * from './user';

export const ENTITIES = [
  Comment,
  Flight,
  Rating,
  Sight,
  Transport,
  Role,
  Sex,
  User,
];