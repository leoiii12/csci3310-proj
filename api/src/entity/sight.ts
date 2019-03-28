import {
    Column, CreateDateColumn, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn,
} from 'typeorm';

import { Comment, CommentDto, Rating, RatingDto } from './';
import { User } from './user';

@Entity('sight')
export class Sight {

  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  title: string;

  @Column('simple-json')
  latLng: { latitude?: number; longitude?: number; } = {};

  @OneToMany(type => Comment, c => c.sight)
  comments: Comment[];

  @OneToMany(type => Rating, r => r.sight)
  ratings: Rating[];

  @ManyToOne(type => User, u => u.sights)
  createUser: User;

  @Column()
  createUserId: number;

  @CreateDateColumn()
  createDate: Date;

}

export class SightDto {

  constructor(
    public id: number,
    public title: string,
    public comments: CommentDto[],
    public ratings: RatingDto[],
  ) {
  }

  static from(sight: Sight): SightDto {
    return new SightDto(
      sight.id,
      sight.title,
      sight.comments,
      sight.ratings,
    );
  }

}

export class SightListDto {

  constructor(
    public id: number,
    public title: string,
  ) {
  }

  static from(sight: Sight): SightListDto {
    return new SightListDto(
      sight.id,
      sight.title,
    );
  }

}
