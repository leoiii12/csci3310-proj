import {
    loginWithServicePrincipalSecretWithAuthResponse, TokenCredentialsBase,
} from '@azure/ms-rest-nodeauth';

import { InternalServerError } from './error';

export class Azure {

  private static credentials: TokenCredentialsBase;

  private static tenantId: string;
  private static clientId: string;
  private static clientSecret: string;

  private static storageAccountName: string;
  private static storageAccountKey: string;

  static getTenantId() {
    if (Azure.tenantId) {
      return Azure.tenantId;
    }
    if (process.env.AZ_TENANT_ID === undefined) {
      throw new InternalServerError('AZ_TENANT_ID undefined.');
    }

    Azure.tenantId = process.env.AZ_TENANT_ID;

    return Azure.tenantId;
  }

  static getClientId() {
    if (Azure.clientId) {
      return Azure.clientId;
    }
    if (process.env.AZ_CLIENT_ID === undefined) {
      throw new InternalServerError('AZ_CLIENT_ID undefined.');
    }

    Azure.clientId = process.env.AZ_CLIENT_ID;

    return Azure.clientId;
  }

  static getClientSecret() {
    if (Azure.clientSecret) {
      return Azure.clientSecret;
    }
    if (process.env.AZ_CLIENT_SECRET === undefined) {
      throw new InternalServerError('AZ_CLIENT_SECRET undefined.');
    }

    Azure.clientSecret = process.env.AZ_CLIENT_SECRET;

    return Azure.clientSecret;
  }

  static async getCredentials(): Promise<TokenCredentialsBase> {
    if (Azure.credentials) {
      return Azure.credentials;
    }

    const authResponse = await loginWithServicePrincipalSecretWithAuthResponse(Azure.getClientId(), Azure.getClientSecret(), Azure.getTenantId());

    Azure.credentials = authResponse.credentials;

    return Azure.credentials;
  }

  static async getStorageAccount(): Promise<{ name: string; key: string; }> {
    if (Azure.storageAccountName && Azure.storageAccountKey) {
      return { name: Azure.storageAccountName, key: Azure.storageAccountKey };
    }
    if (process.env.AZ_STORAGE_ACCOUNT_NAME === undefined) {
      throw new InternalServerError('AZ_STORAGE_ACCOUNT_NAME undefined.');
    }
    if (process.env.AZ_STORAGE_ACCOUNT_KEY === undefined) {
      throw new InternalServerError('AZ_STORAGE_ACCOUNT_KEY undefined.');
    }

    Azure.storageAccountName = process.env.AZ_STORAGE_ACCOUNT_NAME;
    Azure.storageAccountKey = process.env.AZ_STORAGE_ACCOUNT_KEY;

    return { name: Azure.storageAccountName, key: Azure.storageAccountKey };
  }

}
