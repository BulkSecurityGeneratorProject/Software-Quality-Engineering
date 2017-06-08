(function() {
    'use strict';

    angular
        .module('acmeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('technology', {
            parent: 'app',
            url: '/technology',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/technology/technology.html',
                    controller: 'TechnologyController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();
