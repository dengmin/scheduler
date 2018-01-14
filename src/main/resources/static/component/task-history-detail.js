define(['text!tpl/task-history-detail.html'], function (tpl) {
    return {
        template: tpl,
        components: {},
        data: function () {
            var vm = this;
            var fireId = vm.$route.query.fireId;
            var data = {
                taskHistoryDetailLoading: true,
                taskHistoryDetail: {}
            };

            vm.$http.get("/task/history/detail?fireId=" + fireId).then(function (re) {
                data.taskHistoryDetail = re.body.data;
                data.taskHistoryDetailLoading = false;
            });

            return data;
        },
        methods: {
            formatLogContent: function (logContent) {
                var temp = document.createElement ("div");
                (temp.textContent != undefined ) ? (temp.textContent = logContent) : (temp.innerText = logContent);
                var output = temp.innerHTML;
                temp = null;
                return output;
            }
        }
    };
});