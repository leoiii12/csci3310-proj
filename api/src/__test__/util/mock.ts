import { hash } from 'bcryptjs';

import { Role, Sex, User } from '@event/entity';
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
