import {MigrationInterface, QueryRunner} from "typeorm";

export class Init1553527953993 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`CREATE TABLE "user" ("id" int NOT NULL IDENTITY(1,1), "emailAddress" nvarchar(255) NOT NULL, "password" nvarchar(255) NOT NULL, "firstName" nvarchar(255) NOT NULL, "lastName" nvarchar(255) NOT NULL, "sex" int, "roles" ntext NOT NULL, "enrolmentDate" datetime2 NOT NULL CONSTRAINT "DF_1c5406424fabac5d3e62318b618" DEFAULT getdate(), "updateDate" datetime2 NOT NULL CONSTRAINT "DF_b802fcb424617b0cef57d37901f" DEFAULT getdate(), CONSTRAINT "UQ_eea9ba2f6e1bb8cb89c4e672f62" UNIQUE ("emailAddress"), CONSTRAINT "PK_cace4a159ff9f2512dd42373760" PRIMARY KEY ("id"))`);
    }

    public async down(queryRunner: QueryRunner): Promise<any> {
        await queryRunner.query(`DROP TABLE "user"`);
    }

}
