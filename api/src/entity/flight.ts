import {
    Column, CreateDateColumn, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn,
} from 'typeorm';

import { Comment, Rating, User } from './';

@Entity('flight')
export class Flight {

  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  title: string;

  @OneToMany(type => Comment, c => c.flight)
  comments: Comment[];

  @OneToMany(type => Rating, r => r.flight)
  ratings: Rating[];

  @ManyToOne(type => User, u => u.flights)
  createUser: User;

  @Column()
  createUserId: number;

  @CreateDateColumn()
  createDate: Date;

}
