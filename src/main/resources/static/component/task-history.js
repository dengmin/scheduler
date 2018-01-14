define(['text!tpl/task-history.html'], function (tpl) {
    return {
        template: tpl,
        data: function () {
            var vm = this;
            var data = {
                queryLoading: false,
                queryFormModel: {
                    name: '',
                    group: '',
                    execState: '',
                    firedWay: '',
                    page: 1
                },
                currentQueryModel: null,
                queryResult: {},
                taskGroups: []
            };
            vm.$http.get("/task/groups").then(function (re) {
                vm.taskGroups = re.body.data;
            });
            return data;
        },
        mounted: function () {
            this.query();
        },
        methods : {
            query: function () {
                var vm = this;
                var queryFormModel = vm.queryFormModel;
                var queryModel = {
                    name: queryFormModel.name,
                    group: queryFormModel.group,
                    execState: queryFormModel.execState,
                    firedWay: queryFormModel.firedWay,
                    page: 1
                };
                vm.load(queryModel);
            },
            load: function (queryModel) {
                var vm = this;

                vm.currentQueryModel = queryModel;

                vm.queryLoading = true;
                vm.$http.get("/task/history", {params: queryModel}).then(function (re) {
                    vm.queryLoading = false;
                    vm.queryResult = re.body.data;
                }, function () {
                    vm.queryLoading = false;
                    vm.queryResult = {};
                });
            },
            reload: function () {
                this.load(this.currentQueryModel);
            },
            showTaskHistoryDetail: function (fireId) {
                this.$router.push("/task/history/detail?fireId=" + fireId);
            },
            changePage: function (val) {
                this.currentQueryModel.page = val;
                this.load(this.currentQueryModel);
            },
            resolveRowClass: function (row, index) {
                return row.state === 'SUCCESS' ? "row-success" : row.state === 'FAIL' ? "row-fail" : row.state === 'VETOED' ? "row-warning" : "";
            }
        }
    }
});