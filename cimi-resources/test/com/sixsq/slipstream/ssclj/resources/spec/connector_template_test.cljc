(ns com.sixsq.slipstream.ssclj.resources.spec.connector-template-test
  (:require
    [clojure.test :refer :all]
    [clojure.spec.alpha :as s]

    [com.sixsq.slipstream.ssclj.app.params :as p]
    [com.sixsq.slipstream.ssclj.resources.connector-template :as ct]
    [com.sixsq.slipstream.ssclj.resources.spec.connector-template :as cts]
    [com.sixsq.slipstream.ssclj.util.spec :as su]))

(s/def :cimi.test/connector-template (su/only-keys-maps cts/resource-keys-spec))

(def valid-acl {:owner {:principal "ADMIN"
                        :type      "ROLE"}
                :rules [{:principal "ANON"
                         :type      "ROLE"
                         :right     "VIEW"}]})

(deftest test-schema-check
  (let [timestamp "1964-08-25T10:00:00.0Z"
        root {:id                  (str ct/resource-url "/uuid")
              :resourceURI         ct/resource-uri
              :created             timestamp
              :updated             timestamp
              :acl                 valid-acl
              :cloudServiceType    "cloud-software-solution"
              :orchestratorImageid "123"
              :quotaVm             "20"
              :maxIaasWorkers      5
              :instanceName        "foo"}]
    (is (s/valid? :cimi.test/connector-template root))
    (doseq [k (into #{} (keys (dissoc root :id :resourceURI)))]
      (is (not (s/valid? :cimi.test/connector-template (dissoc root k)))))))
