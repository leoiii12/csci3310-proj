import {
    Column, CreateDateColumn, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn,
} from 'typeorm';

import { Comment, Rating, User } from './';

@Entity('transport')
export class Transport {

  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  title: string;

  @OneToMany(type => Comment, c => c.transport)
  comments: Comment[];

  @OneToMany(type => Rating, r => r.transport)
  ratings: Rating[];

  @ManyToOne(type => User, u => u.transports)
  createUser: User;

  @Column()
  createUserId: number;

  @CreateDateColumn()
  createDate: Date;

}
