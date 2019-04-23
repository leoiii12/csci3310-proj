import { Column, CreateDateColumn, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';

import { IsDouble } from '@event/util';

import { Flight, Hotel, Sight, User } from './';
import { UserDto } from './user';

@Entity('rating')
export class Rating {

  @PrimaryGeneratedColumn()
  id: number;

  @Column('float')
  value: number;

  @ManyToOne(type => User, u => u.ratings)
  createUser: User;

  @Column()
  createUserId: number;

  @CreateDateColumn()
  createDate: Date;

  @ManyToOne(type => Flight, f => f.ratings)
  flight: Flight;

  @Column({ nullable: true })
  flightId: number;

  @ManyToOne(type => Sight, s => s.ratings)
  sight: Sight;

  @Column({ nullable: true })
  sightId: number;

  @ManyToOne(type => Hotel, t => t.ratings)
  hotel: Hotel;

  @Column({ nullable: true })
  hotelId: number;

}

export class RatingDto {

  constructor(
    public id: number,
    @IsDouble public value: number,
    public createUser: UserDto,
  ) {
  }

  static from(rating: Rating): RatingDto {
    return new RatingDto(
      rating.id,
      rating.value,
      rating.createUser,
    );
  }

}
