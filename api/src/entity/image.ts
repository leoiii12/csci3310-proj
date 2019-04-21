import { Column, CreateDateColumn, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';

import { User } from './';

@Entity('image')
export class Image {

  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  blobUrl: string;

  @Column()
  isPublic: boolean = false;

  @ManyToOne(type => User, u => u.images)
  createUser: User;

  @Column()
  createUserId: number;

  @CreateDateColumn()
  createDate: Date;

}

export class ImageDto {

  constructor(
    public id: number,
    public blobUrl: string,
  ) {
  }

  static from(image: Image): ImageDto {
    return new ImageDto(
      image.id,
      image.blobUrl,
    );
  }

}
