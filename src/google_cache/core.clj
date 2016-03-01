(ns google-cache.core
  (:require [google-cache.backend :as backend]
            [catacumba.core       :as ct]
            [catacumba.http       :as http]
;            [catacumba.handlers.misc :refer (autoreloader)]
            [taoensso.timbre      :as log])
  (:gen-class))

(defn error-handler [context error]
  "Error handler"
  (clojure.pprint/pprint error)
  (log/error (.getMessage error))
  (let [stacktrace (clojure.string/join "\n" (.getStackTrace error))]
    (log/error (str "Stacktrace: " stacktrace)))
  (http/internal-server-error "{\"error\":\"Internal error\"}"))

(defn geocode-handler
  "Geocoding requests handler"
  [context]
  (if-let [address (get-in context [:query-params :address])]
    (http/ok (backend/lookup address))
    (http/bad-request "{\"error\":\"Address param is empty\"}")))

(def routes
  (ct/routes [[:error error-handler]
              ; [:any (autoreloader)]
              [:get "geocode" geocode-handler]
             ]))

(defn -main
  "Cacher entry point"
  [& args]
  (if (empty? (System/getenv "CACHER_HOST"))
    (log/error "CACHER_HOST needs to be set")
    (ct/run-server routes)))
