import { hash } from 'bcryptjs';

import { Image, Role, Sex, Sight, User } from '@event/entity';
import { DB } from '@event/util';

// DON'T REMOVE ANY STRUCTURE HERE
export const users = {
  admin_1: {
    id: 1,
    emailAddress: 'mkchoi6@cse.cuhk.edu.hk',
    password: '123456',
    roles: [Role.Users, Role.Admins],
    sex: Sex.Male,
    firstName: 'Leo',
    lastName: 'Choi',
  },
  user_1: {
    id: 2,
    emailAddress: '88888889@cuhk.edu.hk',
    password: '123456',
    roles: [Role.Users],
    sex: Sex.Male,
    firstName: 'Tim',
    lastName: 'Wong',
  },
};

export const images = {
  image_1: {
    id: 1,
    blobUrl: '',
    createUserId: users.user_1.id,
  },
};

export const sights = {
  sight_1: {
    id: 1,
    title: '君子塔',
    latLng: {
      latitude: 22.4207731,
      longitude: 114.2075137,
    },
    imageIds: [
      images.image_1.id,
    ],
    createUserId: users.user_1.id,
  },
};

export const init = async () => {
  const connection = await DB.getConnection();
  await connection.synchronize();
};

export const createMockAccounts = async () => {
  const connection = await DB.getConnection();
  const userRepository = connection.getRepository(User);

  for (const user of Object.values(users)) {
    const u = new User();
    u.id = user.id;
    u.emailAddress = user.emailAddress;
    u.password = await hash(user.password, 12);
    u.roles = user.roles;
    u.sex = user.sex;
    u.firstName = user.firstName;
    u.lastName = user.lastName;

    await userRepository.insert(u);
  }
};

export const createMockSights = async () => {
  const connection = await DB.getConnection();
  const sightRepository = connection.getRepository(Sight);
  const imageRepository = connection.getRepository(Image);

  for (const image of Object.values(images)) {
    const i = new Image();
    i.id = image.id;
    i.blobUrl = image.blobUrl;
    i.isPublic = true;
    i.createUserId = image.createUserId;

    await imageRepository.insert(i);
  }

  for (const sight of Object.values(sights)) {
    const s = new Sight();
    s.id = sight.id;
    s.title = sight.title;
    s.latLng = sight.latLng;
    s.imageIds = sight.imageIds;
    s.createUserId = sight.createUserId;

    await sightRepository.insert(s);
  }
};
