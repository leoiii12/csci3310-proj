import {
    Column, CreateDateColumn, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn,
} from 'typeorm';

import { Comment, CommentDto, Image, ImageDto, LatLng, Rating, RatingDto, User } from './';

@Entity('flight')
export class Flight {

  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  title: string;

  @Column('simple-array', { default: '' })
  imageIds: number[] = [];

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

export class FlightDto {

  constructor(
    public id: number,
    public title: string,
    public images: ImageDto[],
    public comments: CommentDto[],
    public ratings: RatingDto[],
  ) {
  }

  static from(sight: Flight, images: Image[]): FlightDto {
    return new FlightDto(
      sight.id,
      sight.title,
      images.map(i => ImageDto.from(i)),
      sight.comments,
      sight.ratings,
    );
  }

}

export class FlightListDto {

  constructor(
    public id: number,
    public title: string,
    public image: ImageDto,
  ) {
  }

  static from(sight: Flight, imagesDict: { [id: number]: Image }): FlightListDto {
    return new FlightListDto(
      sight.id,
      sight.title,
      ImageDto.from(imagesDict[sight.imageIds[0]]),
    );
  }

}
