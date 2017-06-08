(function () {
	'use strict';

	angular
		.module('acmeApp')
		.config(stateConfig);

	stateConfig.$inject = ['$stateProvider'];

	function stateConfig($stateProvider) {
		$stateProvider.state('contact', {
			parent: 'app',
			url: '/contact',
			data: {
				roles: []
			},
			views: {
				'content@': {
					templateUrl: 'app/contact/contact.html',
					controller: 'ContactController',
					controllerAs: 'vm'
				}
			}
		});
	}
})();
