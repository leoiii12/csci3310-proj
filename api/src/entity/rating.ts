import { Column, CreateDateColumn, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';

import { Flight, Sight, Transport, User } from './';

@Entity('rating')
export class Rating {

  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  value: number;

  @ManyToOne(type => User, u => u.comments)
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

  @ManyToOne(type => Transport, t => t.ratings)
  transport: Transport;

  @Column({ nullable: true })
  transportId: number;

}

export class RatingDto {

  constructor(
    public id: number,
    public value: number,
  ) {
  }

  static from(rating: Rating): RatingDto {
    return new RatingDto(
      rating.id,
      rating.value,
    );
  }

}
