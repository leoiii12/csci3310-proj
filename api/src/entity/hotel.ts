import {
    Column, CreateDateColumn, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn,
} from 'typeorm';

import { Comment, CommentDto, Image, ImageDto, LatLng, Rating, RatingDto, User } from './';

@Entity('hotel')
export class Hotel {

  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  title: string;

  @Column('simple-json')
  latLng: { latitude?: number; longitude?: number; } = {};

  @Column('simple-array', { default: '' })
  imageIds: number[] = [];

  @OneToMany(type => Comment, c => c.hotel)
  comments: Comment[];

  @OneToMany(type => Rating, r => r.hotel)
  ratings: Rating[];

  @ManyToOne(type => User, u => u.hotels)
  createUser: User;

  @Column()
  createUserId: number;

  @CreateDateColumn()
  createDate: Date;

}


export class HotelDto {

  constructor(
    public id: number,
    public title: string,
    public latLng: LatLng,
    public images: ImageDto[],
    public comments: CommentDto[],
    public ratings: RatingDto[],
  ) {
  }

  static from(sight: Hotel, images: Image[]): HotelDto {
    return new HotelDto(
      sight.id,
      sight.title,
      new LatLng(sight.latLng.latitude, sight.latLng.longitude),
      images.map(i => ImageDto.from(i)),
      sight.comments,
      sight.ratings,
    );
  }

}

export class HotelListDto {

  constructor(
    public id: number,
    public title: string,
    public image: ImageDto,
  ) {
  }

  static from(sight: Hotel, imagesDict: { [id: number]: Image }): HotelListDto {
    return new HotelListDto(
      sight.id,
      sight.title,
      ImageDto.from(imagesDict[sight.imageIds[0]]),
    );
  }

}
