(function () {
	'use strict';

	angular
		.module('acmeApp')
		.config(stateConfig);

	stateConfig.$inject = ['$stateProvider'];

	function stateConfig($stateProvider) {
		$stateProvider.state('password', {
			parent: 'account',
			url: '/password',
			data: {
				roles: ['USER', 'EMPLOYEE', 'MANAGER', 'ADMIN'],
				pageTitle: 'Password'
			},
			views: {
				'content@': {
					templateUrl: 'app/account/password/password.html',
					controller: 'PasswordController',
					controllerAs: 'vm'
				}
			}
		});
	}
})();
