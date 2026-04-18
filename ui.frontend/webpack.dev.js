const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const SOURCE_ROOT = __dirname + "/src/main/webpack";

const RESOURCE_PATH = "/etc.clientlibs/mvw/resources";

module.exports = env => {

    const writeToDisk = true; // Always write to disk for dev server

    return merge(common, {
        mode: 'development',
        performance: {
            hints: 'warning',
            maxAssetSize: 1048576,
            maxEntrypointSize: 1048576
        },
        plugins: [
            new HtmlWebpackPlugin({
                template: path.resolve(__dirname, SOURCE_ROOT + '/static/index.html')
            })
        ],
        devServer: {
            port: 8080,
            static: {
                directory: path.join(__dirname, 'dist'),
            },
            proxy: [{
                changeOrigin: true,
                context: ["/content", "/etc.clientlibs", "/etc/", "/api"],
                target: 'http://localhost:4502',
                headers: {
                    Authorization: "Basic YWRtaW46YWRtaW4=",
                }, pathRewrite: {
                    "^/content/([^?]*)": "/content/$1?wcmmode=disabled",
                    "^/api/([^?]*)": "/api/$1?wcmmode=disabled",
                }, bypass: function (req, res, proxyOptions) {
                    if (req.url.indexOf(RESOURCE_PATH) > -1) {
                        const resourceUrl = req.url.replace(RESOURCE_PATH, "/clientlib-site/resources");
                        console.log("Skipping proxy for browser request.");
                        console.log("Routing local path: from: ", req.url, ", to: ", resourceUrl);
                        return resourceUrl;
                    }
                    if (req.url.startsWith("/etc.clientlibs/mvw/clientlibs/clientlib-site.")) {
                        const newUrl = "/clientlib-site/site" + (req.url.endsWith("js") ? ".js" : ".css");
                        console.log("Skipping proxy for browser request.");
                        console.log("Routing local path: from: ", req.url, ", to: ", newUrl);
                        return newUrl;
                    }
                     if (req.url.startsWith("/etc.clientlibs/mvw/clientlibs/clientlib-newclub.")) {
                        const newUrl = "/clientlib-newclub/newclub" + (req.url.endsWith("js") ? ".js" : ".css");
                        console.log("Skipping proxy for browser request.");
                        console.log("Routing local path: from: ", req.url, ", to: ", newUrl);
                        return newUrl;
                    }
                    if (req.url.startsWith("/etc.clientlibs/mvw/clientlibs/clientlib-tmvc.")) {
                        const newUrl = "/clientlib-tmvc/tmvc" + (req.url.endsWith("js") ? ".js" : ".css");
                        console.log("Skipping proxy for browser request.");
                        console.log("Routing local path: from: ", req.url, ", to: ", newUrl);
                        return newUrl;
                    }
                    if (req.url.startsWith("/etc.clientlibs/mvw/clientlibs/clientlib-legal.")) {
                        const newUrl = "/clientlib-legal/legal" + (req.url.endsWith("js") ? ".js" : ".css");
                        console.log("Skipping proxy for browser request.");
                        console.log("Routing local path: from: ", req.url, ", to: ", newUrl);
                        return newUrl;
                    }
                }
            }],
            client: {
                overlay: {
                    errors: true,
                    warnings: false,
                },
                progress: true,
            },
            watchFiles: ['src/**/*'],
            hot: true,
            liveReload: true,
            devMiddleware: {
                writeToDisk: writeToDisk,
            }
        }
    });
}
