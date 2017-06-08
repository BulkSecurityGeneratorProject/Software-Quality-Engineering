(function() {
    'use strict';

    angular
        .module('acmeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('products', {
            parent: 'app',
            url: '/products',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/products/products.html',
                    controller: 'ProductsController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();
