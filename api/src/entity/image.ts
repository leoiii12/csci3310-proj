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
