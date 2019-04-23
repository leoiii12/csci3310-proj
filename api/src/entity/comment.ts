import { Column, CreateDateColumn, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';

import { Flight, Hotel, Sight, User } from './';
import { UserDto } from './user';

@Entity('comment')
export class Comment {

  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  content: string;

  @ManyToOne(type => User, u => u.comments)
  createUser: User;

  @Column()
  createUserId: number;

  @CreateDateColumn()
  createDate: Date;

  @ManyToOne(type => Flight)
  flight: Flight;

  @Column({ nullable: true })
  flightId: number;

  @ManyToOne(type => Sight)
  sight: Sight;

  @Column({ nullable: true })
  sightId: number;

  @ManyToOne(type => Hotel)
  hotel: Hotel;

  @Column({ nullable: true })
  hotelId: number;

}

export class CommentDto {

  constructor(
    public id: number,
    public content: string,
    public createUser: UserDto,
    public createDate: Date,
  ) {
  }

  static from(comment: Comment): CommentDto {
    return new CommentDto(
      comment.id,
      comment.content,
      comment.createUser,
      comment.createDate,
    );
  }

}
