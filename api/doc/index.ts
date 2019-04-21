import * as fs from 'fs';
import { dump } from 'js-yaml';
import { isTemplateSpan } from 'typescript';

import { dtoEntries } from './dto';
import { functionEntries } from './function';
import { SwaggerDefinitionProperties, SwaggerFile, SwaggerPath } from './swagger';
import { extractClassAndImportPath, normalizeToSwaggerType } from './util';

const pkg: any = JSON.parse(fs.readFileSync('package.json').toString());

const swaggerFile: SwaggerFile = {
  swagger: '2.0',
  info: {
    version: pkg.version,
    title: 'event331-api',
  },
  host: 'event331-api.azurewebsites.net',
  basePath: '/',
  schemes: [
    'https',
  ],
  paths: {},
  definitions: {},
};

for (const { filePath, inputClassName, outputClassName } of functionEntries) {
  const functionJsonPath = filePath.replace('index.ts', 'function.json');

  const functionJsonString = fs.readFileSync(functionJsonPath).toString();
  const functionJson = JSON.parse(functionJsonString);

  if (functionJsonString.includes('httpTrigger') === false) {
    continue;
  }

  const route = `/api/${functionJson.bindings[0].route}`;

  const regex = /\/api\/([a-zA-Z]+)(\/[a-zA-Z]+)?/;
  const matches = regex.exec(route);
  if (matches === null) {
    throw new Error();
  }

  const tag = matches[1];

  // /api/Auth/Authenticate -> api_Auth_Authenticate
  const operationId = route.replace(/\//g, '_').slice(1);

  const path: SwaggerPath = {
    post: {
      operationId,
      tags: [
        tag,
      ],
      consumes: [
        'application/json',
      ],
      produces: [
        'application/json',
      ],
      parameters: [
      ],
      responses: {
        200: {
          description: 'Success',
        },
      },
    },
  };

  if (inputClassName !== undefined) {
    const parameter = {
      name: 'input',
      in: 'body',
      required: true,
      schema: {
        $ref: `#/definitions/${inputClassName}`,
      },
    };

    path.post.parameters.push(parameter);
  }

  if (outputClassName !== undefined) {
    path.post.responses[200].schema = {
      $ref: `#/definitions/${outputClassName}`,
    };
  }

  path.post.parameters.push({
    in: 'header',
    name: 'X-Authorization',
    type: 'string',
    required: false,
    default: '',
  });

  swaggerFile.paths[route] = path;
}

for (const dtoEntry of dtoEntries) {
  const definition = {
    type: 'object',
    properties: {} as SwaggerDefinitionProperties,
  };

  for (const member of dtoEntry.members) {
    let type = member.type;
    if (type.includes('import')) {
      const { className } = extractClassAndImportPath(member.type);

      type = `#/definitions/${className}`;

      definition.properties[member.name] = type.endsWith('[]') ?
        {
          type: 'array',
          items: {
            $ref: normalizeToSwaggerType(type.replace('[]', '')),
          },
        } :
        {
          $ref: normalizeToSwaggerType(type),
        };

      if (member.decorators.length > 0) {
        definition.properties[member.name].description = member.decorators.join(' ');
      }

      continue;
    }

    if (type.endsWith('[]')) {
      definition.properties[member.name] = {
        type: 'array',
        items: {
          type: type === 'number[]' ? 'integer' : normalizeToSwaggerType(type.replace('[]', '')),
        },
      };
    } else {
      definition.properties[member.name] = {
        type: normalizeToSwaggerType(type),
      };
    }

    if (type === 'number') {
      definition.properties[member.name].type = 'integer';
      definition.properties[member.name].format = undefined;
    }
    if (member.decorators.includes('@IsNumber()') || member.decorators.includes('@IsDouble')) {
      definition.properties[member.name].type = 'number';
      definition.properties[member.name].format = 'double';
    }

    if (member.decorators.length > 0) {
      definition.properties[member.name].description = member.decorators.join(' ');
    }
  }

  swaggerFile.definitions[dtoEntry.className] = definition;
}

export const swaggerFileObj = swaggerFile;
export const swaggerYaml = dump(swaggerFile);
