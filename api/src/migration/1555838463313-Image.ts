import { MigrationInterface, QueryRunner } from 'typeorm';

export class Image1555838463313 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`CREATE TABLE "image" ("id" int NOT NULL IDENTITY(1,1), "blobUrl" nvarchar(255) NOT NULL, "isPublic" bit NOT NULL, "createUserId" int NOT NULL, "createDate" datetime2 NOT NULL CONSTRAINT "DF_857efa134ad2a4edb6e8a206a49" DEFAULT getdate(), CONSTRAINT "PK_d6db1ab4ee9ad9dbe86c64e4cc3" PRIMARY KEY ("id"))`);
        await queryRunner.query(`ALTER TABLE "sight" ADD "imageIds" ntext NOT NULL DEFAULT ''`);
        await queryRunner.query(`ALTER TABLE "image" ADD CONSTRAINT "FK_0cc39ad84c27285b956e9181d79" FOREIGN KEY ("createUserId") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
    }

    public async down(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`ALTER TABLE "image" DROP CONSTRAINT "FK_0cc39ad84c27285b956e9181d79"`);
        await queryRunner.query(`ALTER TABLE "sight" DROP COLUMN "imageIds"`);
        await queryRunner.query(`DROP TABLE "image"`);
    }

}
