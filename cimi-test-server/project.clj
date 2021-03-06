(def +version+ "3.47-SNAPSHOT")

(defproject com.sixsq.slipstream/SlipStreamCljResourcesTestServer-jar "3.47-SNAPSHOT"

  :description "complete test server"

  :url "https://github.com/slipstream/SlipStreamServer"

  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.txt"
            :distribution :repo}

  :plugins [[lein-parent "0.3.2"]]

  :parent-project {:coords  [com.sixsq.slipstream/parent "3.47-SNAPSHOT"]
                   :inherit [:min-lein-version
                             :managed-dependencies
                             :repositories
                             :deploy-repositories]}

  :source-paths ["src"]

  :pom-location "target/"

  :aot [com.sixsq.slipstream.ssclj.app.CIMITestServer]

  :dependencies [[org.apache.curator/curator-test :scope "compile"]
                 [com.sixsq.slipstream/SlipStreamCljResources-jar]
                 [com.sixsq.slipstream/slipstream-ring-container]])
