(function () {
    'use strict';

    angular
        .module('acmeApp')
        .controller('ACMEPassController', ACMEPassController);

    ACMEPassController.$inject = ['$scope', '$state', 'ACMEPass', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ACMEPassController($scope, $state, ACMEPass, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.toggleVisible = toggleVisible;

        loadAll();

        function loadAll() {
            ACMEPass.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.acmePasses = data;
                vm.page = pagingParams.page;
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function toggleVisible(id) {
            // Plan: if the input type is password, change it to text and vice versa.
            for (var i = 0; i < vm.acmePasses.length; i++) {
                var acmePass = vm.acmePasses[i];
                if (acmePass.id == id) {
                    if (!acmePass.visible) {
                        acmePass.visible = true;
                    } else {
                        acmePass.visible = false;
                    }
                }
            }
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
    }
})();
