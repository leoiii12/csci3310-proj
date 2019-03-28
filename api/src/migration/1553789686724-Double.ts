import { MigrationInterface, QueryRunner } from 'typeorm';

export class Double1553789686724 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`ALTER TABLE "rating" ALTER COLUMN "value" float NOT NULL`);
    }

    public async down(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`ALTER TABLE "rating" ALTER COLUMN "value" int NOT NULL`);
    }

}
