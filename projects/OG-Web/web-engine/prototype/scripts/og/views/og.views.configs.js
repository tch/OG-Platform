/**
 * view for securities section
 */
$.register_module({
    name: 'og.views.configs',
    dependencies: [
        'og.api.rest',
        'og.api.text',
        'og.common.masthead.menu',
        'og.common.routes',
        'og.common.search_results.core',
        'og.common.util.history',
        'og.common.util.ui.dialog',
        'og.common.util.ui.message',
        'og.common.util.ui.toolbar',
        'og.views.common.layout',
        'og.views.common.state'
    ],
    obj: function () {
        var api = og.api,
            common = og.common,
            details = common.details,
            history = common.util.history,
            masthead = common.masthead,
            routes = common.routes,
            search = common.search_results.core(),
            ui = common.util.ui,
            layout = og.views.common.layout,
            module = this,
            page_name = 'configs',
            check_state = og.views.common.state.check.partial('/' + page_name),
            details_json = {},
            configs,
            toolbar_buttons = {
                'new': function () {ui.dialog({
                    type: 'input',
                    title: 'Add configuration',
                    fields: [
                        {type: 'input', name: 'Name', id: 'name'},
                        {type: 'textarea', name: 'XML', id: 'xml'}
                    ],
                    buttons: {
                        'Ok': function () {
                            api.rest.configs.put({
                                handler: function (r) {
                                    if (r.error) return ui.dialog({type: 'input', html: r.message});
                                    ui.dialog({type: 'input', action: 'close'});
                                    routes.go(routes.hash(module.rules.load_new_configs,
                                            $.extend({}, routes.last().args, {id: r.meta.id, 'new': true})
                                    ));
                                },
                                name: ui.dialog({return_field_value: 'name'}),
                                xml: ui.dialog({return_field_value: 'xml'})
                            });
                        }
                    }
                })},
                'delete': function () {ui.dialog({
                    type: 'confirm',
                    title: 'Delete configuration?',
                    message: 'Are you sure you want to permanently delete this configuration?',
                    buttons: {
                        'Delete': function () {
                            $(this).dialog('close');
                            api.rest.configs.del({
                                handler: function (r) {
                                    if (r.error) return ui.dialog({type: 'error', message: r.message});
                                    routes.go(routes.hash(module.rules.load_delete,
                                            $.extend({}, routes.last().args, {deleted: true})
                                    ));
                                }, id: routes.last().args.id
                            });
                        }
                    }
                })}
            },
            options = {
                slickgrid: {
                    'selector': '.og-js-results-slick', 'page_type': 'configs',
                    'columns': [
                        {
                            id: 'type', name: 'Type', field: 'type', width: 160,
                            filter_type: 'select',
                            filter_type_options: [
                                'CurrencyMatrix',
                                'CurveSpecificationBuilderConfiguration',
                                'SimpleCurrencyMatrix',
                                'TimeSeriesMetaDataConfiguration',
                                'ViewDefinition',
                                'VolatilitySurfaceSpecification',
                                'VolatilitySurfaceDefinition',
                                'YieldCurveDefinition'
                            ]
                        },
                        {
                            id: 'name', name: 'Name', field: 'name', width: 300, cssClass: 'og-link',
                            filter_type: 'input'
                        }
                    ]
                },
                toolbar: {
                    'default': {
                        buttons: [
                            {name: 'new', handler: toolbar_buttons['new']},
                            {name: 'up', enabled: 'OG-disabled'},
                            {name: 'edit', enabled: 'OG-disabled'},
                            {name: 'delete', enabled: 'OG-disabled'},
                            {name: 'favorites', enabled: 'OG-disabled'}
                        ],
                        location: '.OG-toolbar .og-js-buttons'
                    },
                    active: {
                        buttons: [
                            {name: 'new', handler: toolbar_buttons['new']},
                            {name: 'up', handler: 'handler'},
                            {name: 'edit', handler: 'handler'},
                            {name: 'delete', handler: toolbar_buttons['delete']},
                            {name: 'favorites', handler: 'handler'}
                        ],
                        location: '.OG-toolbar .og-js-buttons'
                    }
                }
            },
            load_configs_without = function (field, args) {
                check_state({args: args, conditions: [{new_page: configs.load, stop: true}]});
                delete args[field];
                configs.search(args);
                routes.go(routes.hash(module.rules.load_configs, args));
            },
            default_page = function () {
                api.text({module: 'og.views.default', handler: function (template) {
                    $.tmpl(template, {
                        name: 'Configs',
                        favorites_list: history.get_html('history.configs.favorites') || 'no favorited configs',
                        recent_list: history.get_html('history.configs.recent') || 'no recently viewed configs',
                        new_list: history.get_html('history.configs.new') || 'no new configs'
                    }).appendTo($('#OG-details .og-main').empty());
                }});
            },
            state = {};
        module.rules = {
            load: {route: '/' + page_name + '/name:?/type:?', method: module.name + '.load'},
            load_filter: {route: '/' + page_name + '/filter:/:id?/name:?/type:?',
                    method: module.name + '.load_filter'},
            load_delete: {route: '/' + page_name + '/deleted:/name:?/type:?',
                    method: module.name + '.load_delete'},
            load_configs: {
                route: '/' + page_name + '/:id/name:?/type:?', method: module.name + '.load_' + page_name
            },
            load_new_configs: {
                route: '/' + page_name + '/:id/new:/name:?/type:?',
                method: module.name + '.load_new_' + page_name
            }
        };
        return configs = {
            load: function (args) {
                check_state({args: args, conditions: [
                    {new_page: function (args) {
                        configs.search(args);
                        masthead.menu.set_tab(page_name);
                        layout('default');
                    }}
                ]});
                if (args.id) return;
                default_page();
                ui.toolbar(options.toolbar['default']);
            },
            load_filter: function (args) {
                check_state({args: args, conditions: [
                    {new_page: function () {
                        state = {filter: true};
                        configs.load(args);
                        args.id
                            ? routes.go(routes.hash(module.rules.load_configs, args))
                            : routes.go(routes.hash(module.rules.load, args));
                    }}
                ]});
                search.filter(args);
            },
            load_delete: function (args) {
                configs.search(args);
                routes.go(routes.hash(module.rules.load, {name: args.name}));
            },
            load_new_configs: load_configs_without.partial('new'),
            load_edit_configs: load_configs_without.partial('edit'),
            load_configs: function (args) {
                check_state({args: args, conditions: [{new_page: configs.load}]});
                configs.details(args);
            },
            search: function (args) {
                search.load($.extend(options.slickgrid, {url: args}));
            },
            details: function (args) {
                ui.toolbar(options.toolbar.active);
                api.rest.configs.get({
                    handler: function (result) {
                        if (result.error) return alert(result.message);
                        details_json = result.data;
                        history.put({
                            name: details_json.templateData.name,
                            item: 'history.configs.recent',
                            value: routes.current().hash
                        });
                        api.text({module: module.name, handler: function (template) {
                            var json = details_json.templateData;
                            if (json.configData) json.configData = JSON.stringify(json.configData, null, 4);
                            $.tmpl(template, json).appendTo($('#OG-details .og-main').empty());
                            details.favorites();
                            ui.message({location: '#OG-details', destroy: true});
                            ui.content_editable({
                                attribute: 'data-og-editable',
                                handler: function () {
                                    routes.go(routes.hash(module.rules.load_edit_configs, $.extend(args, {
                                        edit: 'true'
                                    })));
                                }
                            });
                        }});
                    },
                    id: args.id,
                    loading: function () {
                        ui.message({location: '#OG-details', message: {0: 'loading...', 3000: 'still loading...'}});
                    }
                });
            },
            init: function () {for (var rule in module.rules) routes.add(module.rules[rule]);},
            rules: module.rules
        };
    }
});