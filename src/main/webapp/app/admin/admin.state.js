(function () {
	'use strict';

	angular
		.module('acmeApp')
		.config(stateConfig);

	stateConfig.$inject = ['$stateProvider'];

	function stateConfig($stateProvider) {
		$stateProvider.state('admin', {
			abstract: true,
			parent: 'app'
		});
	}
})();
