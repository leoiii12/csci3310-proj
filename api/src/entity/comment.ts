import { Column, CreateDateColumn, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';

import { Flight, Sight, Transport, User } from './';

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

  @ManyToOne(type => Transport)
  transport: Transport;

  @Column({ nullable: true })
  transportId: number;

}

export class CommentDto {

  constructor(
    public id: number,
    public content: string,
  ) {
  }

  static from(comment: Comment): CommentDto {
    return new CommentDto(
      comment.id,
      comment.content,
    );
  }

}
