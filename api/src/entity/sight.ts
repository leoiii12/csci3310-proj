import {
    Column, CreateDateColumn, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn,
} from 'typeorm';

import { Comment, CommentDto, Image, ImageDto, Rating, RatingDto, User } from './';

@Entity('sight')
export class Sight {

  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  title: string;

  @Column('simple-json')
  latLng: { latitude?: number; longitude?: number; } = {};

  @Column('simple-array', { default: '' })
  imageIds: number[] = [];

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
    public images: ImageDto[],
    public comments: CommentDto[],
    public ratings: RatingDto[],
  ) {
  }

  static from(sight: Sight, images: Image[]): SightDto {
    return new SightDto(
      sight.id,
      sight.title,
      images.map(i => ImageDto.from(i)),
      sight.comments,
      sight.ratings,
    );
  }

}

export class SightListDto {

  constructor(
    public id: number,
    public title: string,
    public image: ImageDto,
  ) {
  }

  static from(sight: Sight, imagesDict: { [id: number]: Image }): SightListDto {
    return new SightListDto(
      sight.id,
      sight.title,
      ImageDto.from(imagesDict[sight.imageIds[0]]),
    );
  }

}
