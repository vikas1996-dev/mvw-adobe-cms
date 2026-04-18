/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

const path = require('path');

const BUILD_DIR = path.join(__dirname, 'dist');
const CLIENTLIB_DIR = path.join(
  __dirname,
  '..',
  'ui.apps',
  'src',
  'main',
  'content',
  'jcr_root',
  'apps',
  'mvw',
  'clientlibs'
);

const libsBaseConfig = {
  allowProxy: true,
  serializationFormat: 'xml',
  cssProcessor: ['default:none', 'min:none'],
  jsProcessor: ['default:none', 'min:none']
};

// Config for `aem-clientlib-generator`
module.exports = {
  context: BUILD_DIR,
  clientLibRoot: CLIENTLIB_DIR,
  libs: [
    {
      ...libsBaseConfig,
      name: 'clientlib-dependencies',
      categories: ['mvw.dependencies'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-dependencies',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-dependencies',
          files: ['**/*.css'],
          flatten: false
        }
      }
    },
    {
      ...libsBaseConfig,
      name: 'clientlib-site',
      categories: ['mvw.site'],
      dependencies: ['mvw.dependencies'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-site',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-site',
          files: ['**/*.css'],
          flatten: false
        },

        // Copy all other files into the `resources` ClientLib directory
        resources: {
          cwd: 'clientlib-site',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    }, {
      ...libsBaseConfig,
      name: 'clientlib-newclub',
      categories: ['newclub.site'],
      dependencies: ['mvw.base'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-newclub',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-newclub',
          files: ['**/*.css'],
          flatten: false
        },

        // Copy all other files into the `resources` ClientLib directory
        resources: {
          cwd: 'clientlib-newclub',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    }, {
      ...libsBaseConfig,
      name: 'clientlib-tmvc',
      categories: ['tmvc.site'],
      dependencies: ['jquery.ui'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-tmvc',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-tmvc',
          files: ['**/*.css'],
          flatten: false
        },

        // Copy all other files into the `resources` ClientLib directory
        resources: {
          cwd: 'clientlib-tmvc',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    }, {
      ...libsBaseConfig,
      name: 'clientlib-legal',
      categories: ['legal.site'],
      dependencies: ['jquery.ui','mvw.dependencies','mvw.base'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-legal',
          files: ['**/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-legal',
          files: ['**/*.css'],
          flatten: false
        },

        // Copy all other files into the `resources` ClientLib directory
        resources: {
          cwd: 'clientlib-legal',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    }, {
      ...libsBaseConfig,
      name: 'clientlib_legal-archivetag',
      categories: ['dam.gui.admin.util','cq.common.wcm','cq.authoring.dialog'],
      dependencies: ['coral.ui','granite.jquery'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-legal_archive',
          files: ['**/*.js'],
          flatten: false
        },
        resources: {
          cwd: 'clientlib-legal_archive',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    }, {
      ...libsBaseConfig,
      name: 'clientlib-legal_assetmanagepublicationtime',
      categories: ['dam.gui.coral.managepublication'],
      dependencies: ['granite.jquery','granite.utils','cq.common.bulktools.collectionstatus'],
      assets: {
        // Copy entrypoint scripts and stylesheets into the respective ClientLib
        // directories
        js: {
          cwd: 'clientlib-legal_assetmanagepublicationtime',
          files: ['**/*.js'],
          flatten: false
        },
        resources: {
          cwd: 'clientlib-legal_assetmanagepublicationtime',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    }, {
      ...libsBaseConfig,
      name: 'clientlib_legal-assetdeletionwarning',
      categories: ['dam.gui.actions.coral'],
      dependencies: ['granite.jquery','granite.ui.foundation','coralui3'],
      assets: {
        js: {
          cwd: 'clientlib-legal_assetdeletion',
          files: ['**/*.js'],
          flatten: false
        },
        resources: {
          cwd: 'clientlib-legal_assetdeletion',
          files: ['**/*.*'],
          flatten: false,
          ignore: ['**/*.js', '**/*.css']
        }
      }
    }
  ]
};
