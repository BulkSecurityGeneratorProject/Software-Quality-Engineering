(function() {
    'use strict';

    angular
        .module('acmeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('media', {
            parent: 'app',
            url: '/media',
            data: {
                authorities: ['ROLE_USER', 'ROLE_EMPLOYEE']
            },
            views: {
                'content@': {
                    templateUrl: 'app/media/media.html',
                    controller: 'MediaController',
                    controllerAs: 'vm'
                }
            }
        }).state('blog1', {
            parent: 'media',
            url: '/blog/blog1',
            data: {
                authorities: ['ROLE_USER', 'ROLE_EMPLOYEE']
            },
            views: {
                'content@': {
                    templateUrl: 'app/media/blog/blog1.html',
                    controller: 'MediaController',
                    controllerAs: 'vm'
                }
            }
        }).state('blog2', {
            parent: 'media',
            url: '/blog/blog2',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/media/blog/blog2.html',
                    controller: 'MediaController',
                    controllerAs: 'vm'
                }
            }
        }).state('news1', {
            parent: 'media',
            url: '/news/story1',
            data: {
                authorities: ['ROLE_USER', 'ROLE_EMPLOYEE']
            },
            views: {
                'content@': {
                    templateUrl: 'app/media/news/story1.html',
                    controller: 'MediaController',
                    controllerAs: 'vm'
                }
            }
        }).state('news2', {
            parent: 'media',
            url: '/news/story2',
            data: {
                authorities: ['ROLE_USER', 'ROLE_EMPLOYEE']
            },
            views: {
                'content@': {
                    templateUrl: 'app/media/news/story2.html',
                    controller: 'MediaController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();
