(defproject google-cache "1.0.0"
  :description "Simple google cacher"
  :url "http://google.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure    "1.8.0"]
                 [funcool/catacumba      "0.11.1"]
                 [com.taoensso/timbre    "4.3.0"]
                 [org.clojure/core.async "0.2.374"]
                 [http-kit               "2.1.19"]]
  :main ^:skip-aot google-cache.core)


