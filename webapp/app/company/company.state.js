(function() {
    'use strict';

    angular
        .module('acmeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('company', {
            parent: 'app',
            url: '/company',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/company/company.html',
                    controller: 'CompanyController',
                    controllerAs: 'vm'
                }
            }
        }).state('resources', {
            parent: 'company',
            url: '/company/resources',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@': {
                    templateUrl: 'app/company/resources/resources.html',
                    controller: 'CompanyController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();
