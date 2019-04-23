import { Comment } from './comment';
import { Flight } from './flight';
import { Hotel } from './hotel';
import { Image } from './image';
import { Rating } from './rating';
import { Sight } from './sight';
import { Role, Sex, User } from './user';

export * from './comment';
export * from './flight';
export * from './image';
export * from './rating';
export * from './sight';
export * from './hotel';
export * from './user';

export const ENTITIES = [
  Comment,
  Flight,
  Image,
  Rating,
  Sight,
  Hotel,
  Role,
  Sex,
  User,
];
