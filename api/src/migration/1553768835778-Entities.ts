import {MigrationInterface, QueryRunner} from "typeorm";

export class Entities1553768835778 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`CREATE TABLE "comment" ("id" int NOT NULL IDENTITY(1,1), "content" nvarchar(255) NOT NULL, "createDate" datetime2 NOT NULL CONSTRAINT "DF_9033a2cea1962a9d5d395ef1a03" DEFAULT getdate(), "flightId" int, "sightId" int, "transportId" int, CONSTRAINT "PK_0b0e4bbc8415ec426f87f3a88e2" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "flight" ("id" int NOT NULL IDENTITY(1,1), "title" nvarchar(255) NOT NULL, "createDate" datetime2 NOT NULL CONSTRAINT "DF_06bb4acd99c91b3b924102d26a0" DEFAULT getdate(), CONSTRAINT "PK_bf571ce6731cf071fc51b94df03" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "rating" ("id" int NOT NULL IDENTITY(1,1), "value" int NOT NULL, "createDate" datetime2 NOT NULL CONSTRAINT "DF_fb9ce64e3355085b5068eede9d4" DEFAULT getdate(), "flightId" int, "sightId" int, "transportId" int, CONSTRAINT "PK_ecda8ad32645327e4765b43649e" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "sight" ("id" int NOT NULL IDENTITY(1,1), "title" nvarchar(255) NOT NULL, "latLng" ntext NOT NULL, "createDate" datetime2 NOT NULL CONSTRAINT "DF_46d1464127d3a188772687ebe66" DEFAULT getdate(), CONSTRAINT "PK_2b63aeff039f2c7134ca011c701" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "transport" ("id" int NOT NULL IDENTITY(1,1), "title" nvarchar(255) NOT NULL, "createDate" datetime2 NOT NULL CONSTRAINT "DF_2af798f404c67b71be45c5637cc" DEFAULT getdate(), CONSTRAINT "PK_298d9594bee72eca3d7a4032a39" PRIMARY KEY ("id"))`);
        await queryRunner.query(`ALTER TABLE "comment" ADD CONSTRAINT "FK_faabf382b00870a83592d315044" FOREIGN KEY ("flightId") REFERENCES "flight"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "comment" ADD CONSTRAINT "FK_1dc235750b6a1b4db158218a28b" FOREIGN KEY ("sightId") REFERENCES "sight"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "comment" ADD CONSTRAINT "FK_740cee7185dddc10f8dfe1d6afd" FOREIGN KEY ("transportId") REFERENCES "transport"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "rating" ADD CONSTRAINT "FK_5dbd7ba8b5e1d31ae84c431d916" FOREIGN KEY ("flightId") REFERENCES "flight"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "rating" ADD CONSTRAINT "FK_bac79581e21116bbba5365c0a31" FOREIGN KEY ("sightId") REFERENCES "sight"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "rating" ADD CONSTRAINT "FK_c726bc0a1f66584869057c16363" FOREIGN KEY ("transportId") REFERENCES "transport"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
    }

    public async down(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`ALTER TABLE "rating" DROP CONSTRAINT "FK_c726bc0a1f66584869057c16363"`);
        await queryRunner.query(`ALTER TABLE "rating" DROP CONSTRAINT "FK_bac79581e21116bbba5365c0a31"`);
        await queryRunner.query(`ALTER TABLE "rating" DROP CONSTRAINT "FK_5dbd7ba8b5e1d31ae84c431d916"`);
        await queryRunner.query(`ALTER TABLE "comment" DROP CONSTRAINT "FK_740cee7185dddc10f8dfe1d6afd"`);
        await queryRunner.query(`ALTER TABLE "comment" DROP CONSTRAINT "FK_1dc235750b6a1b4db158218a28b"`);
        await queryRunner.query(`ALTER TABLE "comment" DROP CONSTRAINT "FK_faabf382b00870a83592d315044"`);
        await queryRunner.query(`DROP TABLE "transport"`);
        await queryRunner.query(`DROP TABLE "sight"`);
        await queryRunner.query(`DROP TABLE "rating"`);
        await queryRunner.query(`DROP TABLE "flight"`);
        await queryRunner.query(`DROP TABLE "comment"`);
    }

}
