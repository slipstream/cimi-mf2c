(ns com.sixsq.slipstream.ssclj.resources.session-template-cyclone-lifecycle-test
  (:require
    [clojure.test :refer :all]
    [com.sixsq.slipstream.ssclj.resources.session-template :as st]
    [com.sixsq.slipstream.ssclj.resources.session-template-cyclone :as cyclone]
    [com.sixsq.slipstream.ssclj.resources.lifecycle-test-utils :as ltu]
    [com.sixsq.slipstream.ssclj.app.params :as p]
    [com.sixsq.slipstream.ssclj.resources.common.utils :as u]
    [com.sixsq.slipstream.ssclj.resources.session-template-lifecycle-test-utils :as stu]))

(use-fixtures :each ltu/with-test-server-fixture)

(def base-uri (str p/service-context (u/de-camelcase st/resource-name)))


(def valid-template {:method      cyclone/authn-method
                     :instance    cyclone/authn-method
                     :name        "CYCLONE"
                     :description "External Authentication via CYCLONE Keycloak"
                     :acl         st/resource-acl})

(deftest lifecycle
  (stu/session-template-lifecycle base-uri valid-template))
