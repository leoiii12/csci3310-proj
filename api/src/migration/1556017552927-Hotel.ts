import {MigrationInterface, QueryRunner} from "typeorm";

export class Hotel1556017552927 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`ALTER TABLE "comment" DROP CONSTRAINT "FK_740cee7185dddc10f8dfe1d6afd"`);
        await queryRunner.query(`ALTER TABLE "rating" DROP CONSTRAINT "FK_c726bc0a1f66584869057c16363"`);
        await queryRunner.query(`EXEC sp_rename "comment.transportId", "hotelId"`);
        await queryRunner.query(`EXEC sp_rename "rating.transportId", "hotelId"`);
        await queryRunner.query(`CREATE TABLE "hotel" ("id" int NOT NULL IDENTITY(1,1), "title" nvarchar(255) NOT NULL, "latLng" ntext NOT NULL, "imageIds" ntext NOT NULL CONSTRAINT "DF_9a9bb5dc2b54058579209bd3e14" DEFAULT '', "createUserId" int NOT NULL, "createDate" datetime2 NOT NULL CONSTRAINT "DF_b6063bf7e1bf397ff272758bcc1" DEFAULT getdate(), CONSTRAINT "PK_3a62ac86b369b36c1a297e9ab26" PRIMARY KEY ("id"))`);
        await queryRunner.query(`ALTER TABLE "flight" ADD "imageIds" ntext NOT NULL`);
        await queryRunner.query(`ALTER TABLE "flight" ADD CONSTRAINT "DF_7d5cba4c7cf47270be4d210e199" DEFAULT '' FOR "imageIds"`);
        await queryRunner.query(`ALTER TABLE "comment" ADD CONSTRAINT "FK_231c6f9d55423791a2c1818eb6c" FOREIGN KEY ("hotelId") REFERENCES "hotel"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "hotel" ADD CONSTRAINT "FK_3262446db26b16f64b07dcba3d5" FOREIGN KEY ("createUserId") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "rating" ADD CONSTRAINT "FK_fa8ca84eb89a928a5917ee52039" FOREIGN KEY ("hotelId") REFERENCES "hotel"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
    }

    public async down(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`ALTER TABLE "rating" DROP CONSTRAINT "FK_fa8ca84eb89a928a5917ee52039"`);
        await queryRunner.query(`ALTER TABLE "hotel" DROP CONSTRAINT "FK_3262446db26b16f64b07dcba3d5"`);
        await queryRunner.query(`ALTER TABLE "comment" DROP CONSTRAINT "FK_231c6f9d55423791a2c1818eb6c"`);
        await queryRunner.query(`ALTER TABLE "flight" DROP CONSTRAINT "DF_7d5cba4c7cf47270be4d210e199"`);
        await queryRunner.query(`ALTER TABLE "flight" DROP COLUMN "imageIds"`);
        await queryRunner.query(`DROP TABLE "hotel"`);
        await queryRunner.query(`EXEC sp_rename "rating.hotelId", "transportId"`);
        await queryRunner.query(`EXEC sp_rename "comment.hotelId", "transportId"`);
        await queryRunner.query(`ALTER TABLE "rating" ADD CONSTRAINT "FK_c726bc0a1f66584869057c16363" FOREIGN KEY ("transportId") REFERENCES "transport"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "comment" ADD CONSTRAINT "FK_740cee7185dddc10f8dfe1d6afd" FOREIGN KEY ("transportId") REFERENCES "transport"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
    }

}
