import {MigrationInterface, QueryRunner} from "typeorm";

export class UserEntities1553771307713 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`ALTER TABLE "comment" ADD "createUserId" int NOT NULL`);
        await queryRunner.query(`ALTER TABLE "flight" ADD "createUserId" int NOT NULL`);
        await queryRunner.query(`ALTER TABLE "rating" ADD "createUserId" int NOT NULL`);
        await queryRunner.query(`ALTER TABLE "sight" ADD "createUserId" int NOT NULL`);
        await queryRunner.query(`ALTER TABLE "transport" ADD "createUserId" int NOT NULL`);
        await queryRunner.query(`ALTER TABLE "comment" ADD CONSTRAINT "FK_74d2a6095f6b3cd4978bd1dbfc9" FOREIGN KEY ("createUserId") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "flight" ADD CONSTRAINT "FK_661f62829c3b38f644e4b8c9b2a" FOREIGN KEY ("createUserId") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "rating" ADD CONSTRAINT "FK_8138a6f49d88b39269f369f20b8" FOREIGN KEY ("createUserId") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "sight" ADD CONSTRAINT "FK_fe68891cbdf0bd47ad35e499bd0" FOREIGN KEY ("createUserId") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "transport" ADD CONSTRAINT "FK_c00ea7288d4faa0820b3b0fdfab" FOREIGN KEY ("createUserId") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
    }

    public async down(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`ALTER TABLE "transport" DROP CONSTRAINT "FK_c00ea7288d4faa0820b3b0fdfab"`);
        await queryRunner.query(`ALTER TABLE "sight" DROP CONSTRAINT "FK_fe68891cbdf0bd47ad35e499bd0"`);
        await queryRunner.query(`ALTER TABLE "rating" DROP CONSTRAINT "FK_8138a6f49d88b39269f369f20b8"`);
        await queryRunner.query(`ALTER TABLE "flight" DROP CONSTRAINT "FK_661f62829c3b38f644e4b8c9b2a"`);
        await queryRunner.query(`ALTER TABLE "comment" DROP CONSTRAINT "FK_74d2a6095f6b3cd4978bd1dbfc9"`);
        await queryRunner.query(`ALTER TABLE "transport" DROP COLUMN "createUserId"`);
        await queryRunner.query(`ALTER TABLE "sight" DROP COLUMN "createUserId"`);
        await queryRunner.query(`ALTER TABLE "rating" DROP COLUMN "createUserId"`);
        await queryRunner.query(`ALTER TABLE "flight" DROP COLUMN "createUserId"`);
        await queryRunner.query(`ALTER TABLE "comment" DROP COLUMN "createUserId"`);
    }

}
