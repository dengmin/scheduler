require.config({
    paths: {
        vue: "js/vue.min",
        vue_router: "js/vue-router.min",
        ELEMENT : "element-ui/index",
        vue_resource: "js/vue-resource.min",
        moment: "js/moment.min",
        text: "js/text"
    },
    urlArgs: 'v=2018011101'
});

require(['vue', 'vue_router', 'vue_resource','ELEMENT', 'moment'], function(Vue,VueRouter,VueResource,Elem, moment){
    Vue.config.silent = true;
    Vue.use(Elem);
    Vue.use(VueRouter);
    Vue.use(VueResource);

    Vue.prototype.$moment = moment;

    var view = {
        index : function (resolver) {
            require(['component/index.js'], resolver);
        },
        tasks : function (resolver) {
            require(['component/tasks.js'], resolver);
        },
        task_edit : function (resolver) {
            require(['component/task-edit.js'], resolver);
        },
        task_history : function (resolver) {
            require(['component/task-history.js'], resolver);
        },
        task_history_detail: function (resolver) {
            require(['component/task-history-detail.js'], resolver);
        }
    };

    require(['component/task-detail.js'],function(taskDetail){
        Vue.prototype.$taskDetailDialog = taskDetail;
    });
    const routes = [
        {
            path: '/',
            component: view.index,
            children: [
                {path: '/tasks', component: view.tasks},
                {path: '/task/new', component: view.task_edit, meta: {editFor: "New"}},
                {path: '/task/edit/:group-:name', component: view.task_edit, meta: {editFor: "Edit"}},
                {path: '/task/history', component: view.task_history},
                {path: '/task/history/detail', component:view.task_history_detail}
            ]
        }
    ];

    const router = new VueRouter({
        routes: routes
    });

    const app = new Vue({
        router: router
    }).$mount('#app');


});