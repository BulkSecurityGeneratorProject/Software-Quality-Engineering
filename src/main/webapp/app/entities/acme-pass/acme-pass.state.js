(function () {
	'use strict';

	angular
		.module('acmeApp')
		.config(stateConfig);

	stateConfig.$inject = ['$stateProvider'];

	function stateConfig($stateProvider) {
		$stateProvider
			.state('acme-pass', {
				parent: 'entity',
				url: '/acme-pass?page&sort&search',
				data: {
					roles: ['USER', 'EMPLOYEE', 'MANAGER', 'ADMIN'],
					pageTitle: 'ACMEPasses'
				},
				views: {
					'content@': {
						templateUrl: 'app/entities/acme-pass/acme-passes.html',
						controller: 'ACMEPassController',
						controllerAs: 'vm'
					}
				},
				params: {
					page: {
						value: '1',
						squash: true
					},
					sort: {
						value: 'id,asc',
						squash: true
					},
					search: null
				},
				resolve: {
					pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
							return {
								page: PaginationUtil.parsePage($stateParams.page),
								sort: $stateParams.sort,
								predicate: PaginationUtil.parsePredicate($stateParams.sort),
								ascending: PaginationUtil.parseAscending($stateParams.sort),
								search: $stateParams.search
							};
						}]
				}
			})
			.state('acme-pass.new', {
				parent: 'acme-pass',
				url: '/new',
				data: {
					roles: ['USER', 'EMPLOYEE', 'MANAGER', 'ADMIN']
				},
				onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
						$uibModal.open({
							templateUrl: 'app/entities/acme-pass/acme-pass-dialog.html',
							controller: 'ACMEPassDialogController',
							controllerAs: 'vm',
							backdrop: 'static',
							size: 'lg',
							resolve: {
								entity: function () {
									return {
										site: null,
										login: null,
										password: null,
										createdDate: null,
										lastModifiedDate: null,
										id: null
									};
								}
							}
						}).result.then(function () {
							$state.go('acme-pass', null, {reload: 'acme-pass'});
						}, function () {
							$state.go('acme-pass');
						});
					}]
			})
			.state('acme-pass.edit', {
				parent: 'acme-pass',
				url: '/{id}/edit',
				data: {
					roles: ['USER', 'EMPLOYEE', 'MANAGER', 'ADMIN']
				},
				onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
						$uibModal.open({
							templateUrl: 'app/entities/acme-pass/acme-pass-dialog.html',
							controller: 'ACMEPassDialogController',
							controllerAs: 'vm',
							backdrop: 'static',
							size: 'lg',
							resolve: {
								entity: ['ACMEPass', function (ACMEPass) {
										return ACMEPass.get({id: $stateParams.id}).$promise;
									}]
							}
						}).result.then(function () {
							$state.go('acme-pass', null, {reload: 'acme-pass'});
						}, function () {
							$state.go('^');
						});
					}]
			})
			.state('acme-pass.delete', {
				parent: 'acme-pass',
				url: '/{id}/delete',
				data: {
					roles: ['USER', 'EMPLOYEE', 'MANAGER', 'ADMIN']
				},
				onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
						$uibModal.open({
							templateUrl: 'app/entities/acme-pass/acme-pass-delete-dialog.html',
							controller: 'ACMEPassDeleteController',
							controllerAs: 'vm',
							size: 'md',
							resolve: {
								entity: ['ACMEPass', function (ACMEPass) {
										return ACMEPass.get({id: $stateParams.id}).$promise;
									}]
							}
						}).result.then(function () {
							$state.go('acme-pass', null, {reload: 'acme-pass'});
						}, function () {
							$state.go('^');
						});
					}]
			});
	}

})();
