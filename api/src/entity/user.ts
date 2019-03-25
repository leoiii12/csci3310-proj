import {
    AfterLoad, Column, CreateDateColumn, Entity, PrimaryGeneratedColumn, UpdateDateColumn,
} from 'typeorm';

export enum Role {
  Users = 1000,
  Admins = 8888,
}

export enum Sex {
  Male = 1000,
  Female = 1001,
}

@Entity('user')
export class User {

  @PrimaryGeneratedColumn()
  id: number;

  @Column({ unique: true })
  emailAddress: string;

  @Column()
  password: string;

  @Column()
  firstName: string = '';

  @Column()
  lastName: string = '';

  @Column({ nullable: true })
  sex: Sex;

  @Column('simple-array')
  roles: Role[] = [];

  @CreateDateColumn()
  enrolmentDate: Date;

  @UpdateDateColumn()
  updateDate: Date;

  @AfterLoad()
  parseRoles() {
    this.roles = this.roles.map((r: any) => parseInt(r, undefined) as Role);
  }
}

export class UserDto {

  constructor(
    public id: number,
    public firstName: string,
    public lastName: string,
    public sex: Sex,
  ) {
  }

  static from(user: User): UserDto {
    return new UserDto(
      user.id,
      user.firstName,
      user.lastName,
      user.sex,
    );
  }

}

export class MyUserDto {

  constructor(
    public id: number,
    public emailAddress: string,
    public firstName: string,
    public lastName: string,
    public sex: Sex,
    public roles: Role[],
  ) {
  }

  static from(user: User): MyUserDto {
    return new MyUserDto(
      user.id,
      user.emailAddress,
      user.firstName,
      user.lastName,
      user.sex,
      user.roles,
    );
  }

}
