(function () {
	'use strict';

	angular
		.module('acmeApp')
		.config(stateConfig);

	stateConfig.$inject = ['$stateProvider'];

	function stateConfig($stateProvider) {
		$stateProvider.state('register', {
			parent: 'account',
			url: '/register',
			data: {
				roles: [],
				pageTitle: 'Registration'
			},
			views: {
				'content@': {
					templateUrl: 'app/account/register/register.html',
					controller: 'RegisterController',
					controllerAs: 'vm'
				}
			}
		});
	}
})();
